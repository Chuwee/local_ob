import { ChannelContent } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsService, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../../container/channel-communication-notifier.service';

const PORTAL_CONTENTS_MAPPING = {
    header: 69,
    mailSending: 70,
    venuePickUp: 71,
    mobileSending: 72,
    whatsapp: 73,
    downloadTickets: 74,
    footer: 75
};

@Component({
    selector: 'app-channel-communication-reallocation-confirm',
    templateUrl: './channel-communication-reallocation-confirm.component.html',
    styleUrls: ['./channel-communication-reallocation-confirm.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelCommunicationReallocationConfirmComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _channelsService = inject(ChannelsService);
    private readonly _channelsExtendedService = inject(ChannelsExtendedService);
    private readonly _messageDialogService = inject(MessageDialogService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _communicationNotifierService = inject(ChannelCommunicationNotifierService);
    private readonly CONTENTS_CATEGORY = 'email';
    private _onDestroy = new Subject<void>();
    private _channelId: number;
    private _selectedLanguage = new BehaviorSubject<string>(null);
    private _contentsIds: typeof PORTAL_CONTENTS_MAPPING = PORTAL_CONTENTS_MAPPING;

    readonly form = this._fb.group({
        subject: undefined as string,
        header: undefined as string,
        deliveryMethods: this._fb.group({
            mailSending: undefined as string,
            venuePickUp: undefined as string,
            mobileSending: undefined as string,
            whatsapp: undefined as string
        }),
        downloadTickets: undefined as string,
        footer: undefined as string
    });

    readonly isInProgress$ = this._channelsExtendedService.isContentsInProgress$();
    readonly languageList$ = this._channelsService.getChannel$()
        .pipe(
            first(Boolean),
            tap(channel => {
                this._channelId = channel.id;
            }),
            map(channel => channel.languages.selected),
            filter(Boolean),
            tap(languages => this._selectedLanguage.next(languages[0])),
            shareReplay(1)
        );

    readonly selectedLanguage$ = this._selectedLanguage.asObservable();

    placeholdersMap: Record<string, string[]> = {};

    ngOnInit(): void {
        this.selectedLanguage$
            .pipe(
                filter(Boolean),
                switchMap(lang => {
                    this.loadContents(lang);
                    return this._channelsExtendedService.getContents$();
                }),
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(contents => {
                const header = contents.find(content => content.id === this._contentsIds.header);
                const mailSending = contents.find(content => content.id === this._contentsIds.mailSending);
                const venuePickUp = contents.find(content => content.id === this._contentsIds.venuePickUp);
                const mobileSending = contents.find(content => content.id === this._contentsIds.mobileSending);
                const whatsapp = contents.find(content => content.id === this._contentsIds.whatsapp);
                const downloadTickets = contents.find(content => content.id === this._contentsIds.downloadTickets);
                const footer = contents.find(content => content.id === this._contentsIds.footer);
                this.placeholdersMap = {
                    header: header?.labels?.map(({ code }) => code),
                    mailSending: mailSending?.labels?.map(({ code }) => code),
                    venuePickUp: venuePickUp?.labels?.map(({ code }) => code),
                    mobileSending: mobileSending?.labels?.map(({ code }) => code),
                    whatsapp: whatsapp?.labels?.map(({ code }) => code),
                    downloadTickets: downloadTickets?.labels?.map(({ code }) => code),
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
                    downloadTickets: downloadTickets?.value,
                    footer: footer?.value
                });
                this.form.markAsPristine();
            });

        this._communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this.loadContents(this._selectedLanguage.getValue()));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._channelsExtendedService.clearContents$();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    };

    save(): void {
        this.save$().subscribe(() => {
            this.loadContents(this._selectedLanguage.getValue());
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const currentLang = this._selectedLanguage.getValue();
            const formValues = this.form.value;
            const header: ChannelContent = {
                id: this._contentsIds.header,
                language: currentLang,
                subject: formValues.subject,
                value: formValues.header
            };
            const mailSending: ChannelContent = {
                id: this._contentsIds.mailSending,
                language: currentLang,
                value: formValues.deliveryMethods.mailSending
            };
            const venuePickUp: ChannelContent = {
                id: this._contentsIds.venuePickUp,
                language: currentLang,
                value: formValues.deliveryMethods.venuePickUp
            };
            const mobileSending: ChannelContent = {
                id: this._contentsIds.mobileSending,
                language: currentLang,
                value: formValues.deliveryMethods.mobileSending
            };
            const whatsapp: ChannelContent = {
                id: this._contentsIds.whatsapp,
                language: currentLang,
                value: formValues.deliveryMethods.whatsapp
            };
            const downloadTickets: ChannelContent = {
                id: this._contentsIds.downloadTickets,
                language: currentLang,
                value: formValues.downloadTickets
            };
            const footer: ChannelContent = {
                id: this._contentsIds.footer,
                language: currentLang,
                value: formValues.footer
            };
            const data = [header, mailSending, venuePickUp, mobileSending, whatsapp, footer];
            if (formValues.downloadTickets != null) {
                data.push(downloadTickets);
            }

            return this._channelsExtendedService.updateContents(this._channelId, this.CONTENTS_CATEGORY, data, currentLang).pipe(
                tap(() => this._ephemeralSrv.showSaveSuccess())
            );
        }
    }

    cancel(): void {
        this.loadContents(this._selectedLanguage.getValue());
    }

    changeLanguage(newLanguage: string): void {
        this._selectedLanguage.next(newLanguage);
    }

    private loadContents(lang: string): void {
        this._channelsExtendedService.loadContents(this._channelId, this.CONTENTS_CATEGORY, lang);
        this.form.markAsPristine();
    }

}
