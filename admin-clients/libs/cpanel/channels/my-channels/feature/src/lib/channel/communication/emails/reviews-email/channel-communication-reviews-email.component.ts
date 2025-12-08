import { ChannelContent } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsExtendedService, channelWebTypes, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder } from '@angular/forms';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../../container/channel-communication-notifier.service';

const CONTENTS_CATEGORY = 'email';
const PORTAL_CONTENTS_MAPPING = {
    header: 201,
    footer: 202
};

@Component({
    selector: 'app-channel-communication-reviews-email',
    templateUrl: './channel-communication-reviews-email.component.html',
    styleUrls: ['./channel-communication-reviews-email.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelCommunicationReviewsEmailComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #fb = inject(NonNullableFormBuilder);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #communicationNotifierSrv = inject(ChannelCommunicationNotifierService);

    #channelId: number;
    #selectedLanguage = new BehaviorSubject<string>(null);
    placeholdersMap: Record<string, string[]> = {};

    readonly form = this.#fb.group({
        subject: '',
        header: '',
        footer: ''
    });

    readonly channelWebTypes = channelWebTypes;
    readonly $loading = toSignal(this.#channelsExtSrv.isContentsInProgress$());
    readonly selectedLanguage$ = this.#selectedLanguage.asObservable();
    readonly languageList$ = this.#channelsSrv.getChannel$()
        .pipe(
            first(Boolean),
            map(channel => {
                this.#channelId = channel.id;
                return channel.languages.selected;
            }),
            filter(Boolean),
            tap(languages => this.#selectedLanguage.next(languages[0])),
            shareReplay(1)
        );

    ngOnInit(): void {
        this.selectedLanguage$
            .pipe(
                filter(Boolean),
                switchMap(lang => {
                    this.#loadContents(lang);
                    return this.#channelsExtSrv.getContents$();
                }),
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(contents => {
                const header = contents.find(content => content.id === PORTAL_CONTENTS_MAPPING.header);
                const footer = contents.find(content => content.id === PORTAL_CONTENTS_MAPPING.footer);

                this.placeholdersMap = {
                    header: header?.labels?.map(({ code }) => code),
                    footer: footer?.labels?.map(({ code }) => code)
                };
                this.form.patchValue({
                    subject: header?.subject,
                    header: header?.value,
                    footer: footer?.value
                });
                this.form.markAsPristine();
            });

        this.#communicationNotifierSrv.getRefreshDataSignal$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.#loadContents(this.#selectedLanguage.getValue()));
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearContents$();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.#validateIfCanChangeLanguage();

    save$(): Observable<void> {
        if (this.form.valid) {
            const currentLang = this.#selectedLanguage.getValue();
            const formValues = this.form.value;
            const header: ChannelContent = {
                id: PORTAL_CONTENTS_MAPPING.header,
                language: currentLang,
                subject: formValues.subject,
                value: formValues.header
            };
            const footer: ChannelContent = {
                id: PORTAL_CONTENTS_MAPPING.footer,
                language: currentLang,
                value: formValues.footer
            };
            const data = [header, footer];
            return this.#channelsExtSrv.updateContents(this.#channelId, CONTENTS_CATEGORY, data, currentLang).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#loadContents(this.#selectedLanguage.getValue());
        });
    }

    cancel(): void {
        this.#loadContents(this.#selectedLanguage.getValue());
    }

    changeLanguage(newLanguage: string): void {
        this.#selectedLanguage.next(newLanguage);
    }

    #loadContents(lang: string): void {
        this.#channelsExtSrv.loadContents(this.#channelId, CONTENTS_CATEGORY, lang);
        this.form.markAsPristine();
    }

    #validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogSrv.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
