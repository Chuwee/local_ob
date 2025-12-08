import { ChannelContent } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelType, ChannelsService, ChannelsExtendedService, channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { MessageDialogService, EphemeralMessageService, LanguageBarComponent, HelpButtonComponent, RichTextAreaComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../../container/channel-communication-notifier.service';

const PORTAL_CONTENTS_MAPPING = {
    header: 169,
    payoutInfo: 170,
    footer: 167
};
@Component({
    selector: 'app-channel-communication-sold-ticket',
    templateUrl: './channel-communication-sold-ticket.component.html',
    styleUrls: ['./channel-communication-sold-ticket.component.scss'],
    imports: [LanguageBarComponent, ReactiveFormsModule, FormContainerComponent,
        HelpButtonComponent, MatLabel, MatFormFieldModule, RichTextAreaComponent,
        TranslatePipe, AsyncPipe, MatProgressSpinner, MatInput, FlexLayoutModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelCommunicationSoldTicketComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #communicationNotifierService = inject(ChannelCommunicationNotifierService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #CONTENTS_CATEGORY = 'email';

    #channelId: number;
    #selectedLanguage = new BehaviorSubject<string>(null);
    #contentsIds: typeof PORTAL_CONTENTS_MAPPING;

    form = this.#fb.group({
        subject: undefined as string,
        header: undefined as string,
        payoutInfo: undefined as string,
        footer: undefined as string
    });

    isNewReceiptEnabled$ = this.#channelsService.getChannel$().pipe(
        map(channel => !!channel?.settings?.v2_receipt_template_enabled)
    );

    isInProgress$ = this.#channelsExtSrv.isContentsInProgress$();
    languageList$ = this.#channelsService.getChannel$()
        .pipe(
            first(Boolean),
            tap(channel => {
                this.#channelId = channel.id;
                this.channelType = channel.type;
                this.#contentsIds = PORTAL_CONTENTS_MAPPING;
            }),
            map(channel => channel.languages.selected),
            filter(Boolean),
            tap(languages => this.#selectedLanguage.next(languages[0])),
            shareReplay(1)
        );

    hasSmartBooking$ = this.#entityService.getEntity$()
        .pipe(
            filter(Boolean),
            map(entity => (entity.settings.external_integration?.custom_managements.filter(
                management => management.type === 'SMART_BOOKING_INTEGRATION'
            ).length > 0)));

    selectedLanguage$ = this.#selectedLanguage.asObservable();
    channelType: ChannelType;
    channelWebTypes = channelWebTypes;
    placeholdersMap: Record<string, string[]> = {};

    ngOnInit(): void {
        this.selectedLanguage$
            .pipe(
                filter(Boolean),
                switchMap(lang => {
                    this.loadContents(lang);
                    return this.#channelsExtSrv.getContents$();
                }),
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(contents => {
                const header = contents.find(content => content.id === this.#contentsIds.header);
                const footer = contents.find(content => content.id === this.#contentsIds.footer);
                const payoutInfo = contents.find(content => content.id === this.#contentsIds.payoutInfo);

                this.placeholdersMap = {
                    header: header?.labels?.map(({ code }) => code),
                    payoutInfo: payoutInfo?.labels?.map(({ code }) => code),
                    footer: footer?.labels?.map(({ code }) => code)

                };
                this.form.patchValue({
                    subject: header?.subject,
                    header: header?.value,
                    footer: footer?.value,
                    payoutInfo: payoutInfo?.value
                });
                this.form.markAsPristine();
            });

        this.#communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.loadContents(this.#selectedLanguage.getValue()));
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearContents$();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    save(): void {
        this.save$().subscribe(() => {
            this.loadContents(this.#selectedLanguage.getValue());
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const currentLang = this.#selectedLanguage.getValue();
            const formValues = this.form.value;
            const header: ChannelContent = {
                id: this.#contentsIds.header,
                language: currentLang,
                subject: formValues.subject,
                value: formValues.header
            };
            const footer: ChannelContent = {
                id: this.#contentsIds.footer,
                language: currentLang,
                value: formValues.footer
            };
            const payoutInfo: ChannelContent = {
                id: this.#contentsIds.payoutInfo,
                language: currentLang,
                value: formValues.payoutInfo
            };
            const data = [header, footer, payoutInfo];

            return this.#channelsExtSrv.updateContents(this.#channelId, this.#CONTENTS_CATEGORY, data, currentLang).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        }
    }

    cancel(): void {
        this.loadContents(this.#selectedLanguage.getValue());
    }

    changeLanguage(newLanguage: string): void {
        this.#selectedLanguage.next(newLanguage);
    }

    private loadContents(lang: string): void {
        this.#channelsExtSrv.loadContents(this.#channelId, this.#CONTENTS_CATEGORY, lang);
        this.form.markAsPristine();
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
