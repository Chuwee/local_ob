import { ChannelContent } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelType, ChannelsService, ChannelsExtendedService, channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../../container/channel-communication-notifier.service';

const PORTAL_CONTENTS_MAPPING = {
    header: 2,
    mailSending: 48,
    venuePickUp: 49,
    mobileSending: 50,
    whatsapp: 67,
    customModule: 81,
    smartBookingCustomModule: 82,
    downloadTickets: 54,
    footer: 26
};

const BOXOFFICE_CONTENTS_MAPPING = {
    header: 2,
    mailSending: 5,
    venuePickUp: 6,
    mobileSending: 7,
    whatsapp: -1,
    customModule: -1,
    smartBookingCustomModule: -1,
    downloadTickets: -1,
    footer: 4
};

@Component({
    selector: 'app-channel-communication-purchase-confirm',
    templateUrl: './channel-communication-purchase-confirm.component.html',
    styleUrls: ['./channel-communication-purchase-confirm.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelCommunicationPurchaseConfirmComponent implements OnInit, OnDestroy {
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
        deliveryMethods: this.#fb.group({
            mailSending: undefined as string,
            venuePickUp: undefined as string,
            mobileSending: undefined as string,
            whatsapp: undefined as string
        }),
        customModule: undefined as string,
        smartBookingCustomModule: undefined as string,
        downloadTickets: undefined as string,
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
                this.#contentsIds = channelWebTypes.includes(this.channelType) ?
                    PORTAL_CONTENTS_MAPPING : BOXOFFICE_CONTENTS_MAPPING;
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
                const mailSending = contents.find(content => content.id === this.#contentsIds.mailSending);
                const venuePickUp = contents.find(content => content.id === this.#contentsIds.venuePickUp);
                const mobileSending = contents.find(content => content.id === this.#contentsIds.mobileSending);
                const whatsapp = contents.find(content => content.id === this.#contentsIds.whatsapp);
                const customModule = contents.find(content => content.id === this.#contentsIds.customModule);
                const smartBookingCustomModule = contents.find(content => content.id === this.#contentsIds.smartBookingCustomModule);
                const downloadTickets = contents.find(content => content.id === this.#contentsIds.downloadTickets);
                const footer = contents.find(content => content.id === this.#contentsIds.footer);
                this.placeholdersMap = {
                    header: header?.labels?.map(({ code }) => code),
                    mailSending: mailSending?.labels?.map(({ code }) => code),
                    venuePickUp: venuePickUp?.labels?.map(({ code }) => code),
                    mobileSending: mobileSending?.labels?.map(({ code }) => code),
                    whatsapp: whatsapp?.labels?.map(({ code }) => code),
                    downloadTickets: downloadTickets?.labels?.map(({ code }) => code),
                    customModule: customModule?.labels?.map(({ code }) => code),
                    smartBookingCustomModule: smartBookingCustomModule?.labels?.map(({ code }) => code),
                    footer: footer?.labels?.map(({ code }) => code)
                };
                this.form.patchValue({
                    subject: header?.subject,
                    header: header?.value,
                    deliveryMethods: {
                        mailSending: mailSending?.value,
                        venuePickUp: venuePickUp?.value,
                        mobileSending: mobileSending?.value,
                        whatsapp: whatsapp?.value
                    },
                    customModule: customModule?.value,
                    smartBookingCustomModule: smartBookingCustomModule?.value,
                    downloadTickets: downloadTickets?.value,
                    footer: footer?.value
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
            const mailSending: ChannelContent = {
                id: this.#contentsIds.mailSending,
                language: currentLang,
                value: formValues.deliveryMethods.mailSending
            };
            const venuePickUp: ChannelContent = {
                id: this.#contentsIds.venuePickUp,
                language: currentLang,
                value: formValues.deliveryMethods.venuePickUp
            };
            const mobileSending: ChannelContent = {
                id: this.#contentsIds.mobileSending,
                language: currentLang,
                value: formValues.deliveryMethods.mobileSending
            };
            const whatsapp: ChannelContent = {
                id: this.#contentsIds.whatsapp,
                language: currentLang,
                value: formValues.deliveryMethods.whatsapp
            };
            const customModule: ChannelContent = {
                id: this.#contentsIds.customModule,
                language: currentLang,
                value: formValues.customModule
            };
            const smartBookingCustomModule: ChannelContent = {
                id: this.#contentsIds.smartBookingCustomModule,
                language: currentLang,
                value: formValues.smartBookingCustomModule
            };
            const downloadTickets: ChannelContent = {
                id: this.#contentsIds.downloadTickets,
                language: currentLang,
                value: formValues.downloadTickets
            };
            const footer: ChannelContent = {
                id: this.#contentsIds.footer,
                language: currentLang,
                value: formValues.footer
            };
            const data = [header, mailSending, venuePickUp, mobileSending, whatsapp, customModule, footer];
            if (formValues.downloadTickets != null) {
                data.push(downloadTickets);
            }
            if (formValues.smartBookingCustomModule != null) {
                data.push(smartBookingCustomModule);
            }

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
