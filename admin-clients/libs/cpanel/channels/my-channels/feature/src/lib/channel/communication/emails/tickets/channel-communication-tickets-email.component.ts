import { ChannelContent } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelType, ChannelsService, ChannelsExtendedService, channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../../container/channel-communication-notifier.service';

const PORTAL_CONTENTS_MAPPING = {
    ticketsEmail: 18
};

const BOXOFFICE_CONTENTS_MAPPING = {
    ticketsEmail: 3
};

@Component({
    selector: 'app-channel-communication-tickets-email',
    templateUrl: './channel-communication-tickets-email.component.html',
    styleUrls: ['./channel-communication-tickets-email.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelCommunicationTicketsEmailComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly CONTENTS_CATEGORY = 'email';
    private _onDestroy = new Subject<void>();
    private _channelId: number;
    private _selectedLanguage = new BehaviorSubject<string>(null);
    private _contentsIds: typeof PORTAL_CONTENTS_MAPPING;

    form: UntypedFormGroup;
    isInProgress$: Observable<boolean>;
    languageList$: Observable<string[]>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    channelType: ChannelType;
    placeholdersMap: Record<string, string[]> = {};

    constructor(
        private _fb: UntypedFormBuilder,
        private _channelsService: ChannelsService,
        private _channelsExtSrv: ChannelsExtendedService,
        private _messageDialogService: MessageDialogService,
        private _ephemeralSrv: EphemeralMessageService,
        private _communicationNotifierService: ChannelCommunicationNotifierService
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({
            subject: undefined,
            body: undefined
        });

        this.isInProgress$ = this._channelsExtSrv.isContentsInProgress$();

        this.languageList$ = this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                tap(channel => {
                    this._channelId = channel.id;
                    this.channelType = channel.type;
                    this._contentsIds = channelWebTypes.includes(this.channelType) ?
                        PORTAL_CONTENTS_MAPPING : BOXOFFICE_CONTENTS_MAPPING;
                }),
                map(channel => channel.languages.selected),
                filter(languages => !!languages),
                tap(languages => this._selectedLanguage.next(languages[0])),
                shareReplay(1)
            );

        this.selectedLanguage$
            .pipe(
                first(lang => !!lang),
                switchMap(lang => {
                    this.loadContents(lang);
                    return this._channelsExtSrv.getContents$();
                }),
                filter(contents => !!contents),
                takeUntil(this._onDestroy)
            )
            .subscribe(contents => {
                const ticketsEmail = contents.find(content => content.id === this._contentsIds.ticketsEmail);
                this.placeholdersMap = {
                    body: ticketsEmail.labels?.map(({ code }) => code)
                };
                this.form.patchValue({
                    subject: ticketsEmail?.subject,
                    body: ticketsEmail?.value
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
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    save(): void {
        this.save$().subscribe(() => {
            this.loadContents(this._selectedLanguage.getValue());

        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const currentLang = this._selectedLanguage.getValue();
            const formValues = this.form.value;
            const ticketsEmail: ChannelContent = {
                id: this._contentsIds.ticketsEmail,
                language: currentLang,
                subject: formValues.subject,
                value: formValues.body
            };
            const data = [ticketsEmail];

            return this._channelsExtSrv.updateContents(this._channelId, this.CONTENTS_CATEGORY, data, currentLang).pipe(
                tap(() => this._ephemeralSrv.showSaveSuccess())
            );
        }
    }

    cancel(): void {
        this.loadContents(this._selectedLanguage.getValue());
    }

    changeLanguage(newLanguage: string): void {
        this.loadContents(newLanguage);
        this._selectedLanguage.next(newLanguage);
    }

    private loadContents(lang: string): void {
        this._channelsExtSrv.loadContents(this._channelId, this.CONTENTS_CATEGORY, lang);
        this.form.markAsPristine();
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
