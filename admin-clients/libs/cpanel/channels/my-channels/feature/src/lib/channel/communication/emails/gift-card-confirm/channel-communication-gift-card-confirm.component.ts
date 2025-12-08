import { ChannelContent } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsService, ChannelsExtendedService, channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, Observable, of, Subject, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../../container/channel-communication-notifier.service';

const PORTAL_CONTENTS_MAPPING = {
    header: 60,
    footer: 61
};

@Component({
    selector: 'app-channel-communication-gift-card-confirm',
    templateUrl: './channel-communication-gift-card-confirm.component.html',
    styleUrls: ['./channel-communication-gift-card-confirm.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelCommunicationGiftCardConfirmComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly CONTENTS_CATEGORY = 'email';
    private _onDestroy = new Subject<void>();
    private _channelId: number;
    private _selectedLanguage = new BehaviorSubject<string>(null);
    private _contentsIds: typeof PORTAL_CONTENTS_MAPPING;

    form: UntypedFormGroup;
    reqInProgress$: Observable<boolean>;
    languageList$: Observable<string[]>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    channelWebTypes = channelWebTypes;
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
            subject: null,
            header: null,
            footer: null
        });

        this.reqInProgress$ = this._channelsExtSrv.isContentsInProgress$();

        this.languageList$ = this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                map(channel => {
                    this._channelId = channel.id;
                    this._contentsIds = PORTAL_CONTENTS_MAPPING;
                    return channel.languages.selected;
                }),
                filter(languages => !!languages),
                tap(languages => this._selectedLanguage.next(languages[0])),
                shareReplay(1)
            );

        this.selectedLanguage$
            .pipe(
                filter(lang => !!lang),
                switchMap(lang => {
                    this.loadContents(lang);
                    return this._channelsExtSrv.getContents$();
                }),
                filter(contents => !!contents),
                takeUntil(this._onDestroy)
            ).subscribe(contents => {
                const header = contents.find(content => content.id === this._contentsIds.header);
                const footer = contents.find(content => content.id === this._contentsIds.footer);
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

        this._communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this.loadContents(this._selectedLanguage.getValue()));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._channelsExtSrv.clearContents$();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

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
            const footer: ChannelContent = {
                id: this._contentsIds.footer,
                language: currentLang,
                value: formValues.footer
            };
            const data = [header, footer];
            return this._channelsExtSrv.updateContents(this._channelId, this.CONTENTS_CATEGORY, data, currentLang)
                .pipe(tap(() => {
                    this._ephemeralSrv.showSaveSuccess();
                }));
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadContents(this._selectedLanguage.getValue());
        });
    }

    cancel(): void {
        this.loadContents(this._selectedLanguage.getValue());
    }

    changeLanguage(newLanguage: string): void {
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
