import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventAvetConnection, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { SessionCommunicationService } from '@admin-clients/cpanel-promoters-events-sessions-communication-data-access';
import {
    SessionTicketContentComponent, SessionChannelContentComponent, SessionTicketPassbookComponent
} from '@admin-clients/cpanel-promoters-events-sessions-communication-feature';
import { EventType } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChildren, QueryList, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-multi-session-communication',
    templateUrl: './multi-session-communication.component.html',
    styleUrls: ['./multi-session-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MultiSessionCommunicationComponent implements OnInit, OnDestroy {

    private _onDestroy = new Subject<void>();
    private _selectedLanguage = new BehaviorSubject<string>(null);
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('ticketContent')
    private _ticketContentComponent: SessionTicketContentComponent;

    @ViewChild('channelContent')
    private _channelContentComponent: SessionChannelContentComponent;

    @ViewChild('passbookContent')
    private _passbookContentComponent: SessionTicketPassbookComponent;

    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    languages$: Observable<string[]>;
    passbookEnabled$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventService: EventsService,
        private _sessionCommunicationService: SessionCommunicationService,
        private _messageDialogService: MessageDialogService,
        private _ephemeralMessage: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        // base form
        this.form = this._fb.group({});

        this.passbookEnabled$ = this._eventService.event.get$().pipe(
            filter(event => !!event),
            map(event => event.type !== EventType.avet ||
                event.type === EventType.avet && event.additional_config?.avet_config === EventAvetConnection.socket)
        );

        this.languages$ = this._eventService.event.get$()
            .pipe(
                take(1),
                map(event => event.settings.languages.selected),
                tap(languages => {
                    // select first language
                    if (languages?.length) {
                        this._selectedLanguage.next(languages[0]);
                    }
                })
            );

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._sessionCommunicationService.isChannelTextsInProgress$(),
            this._sessionCommunicationService.isChannelImagesInProgress$(),
            this._sessionCommunicationService.isChannelImagesDeleteInProgress$(),
            this._sessionCommunicationService.isTicketPdfTextsInProgress$(),
            this._sessionCommunicationService.isTicketPrinterTextsInProgress$(),
            this._sessionCommunicationService.isTicketPdfImagesInProgress$(),
            this._sessionCommunicationService.isTicketPdfImagesDeleteInProgress$(),
            this._sessionCommunicationService.isTicketPrinterImagesInProgress$(),
            this._sessionCommunicationService.isTicketPrinterImagesDeleteInProgress$(),
            this._sessionCommunicationService.isDownloadUrlPassbookPreviewInProgress$(),
            this._sessionCommunicationService.isSessionTicketPassbookContentTextsInProgress$(),
            this._sessionCommunicationService.isSessionTicketPassbookContentTextsSaving$(),
            this._sessionCommunicationService.isSessionTicketPassbookContentImagesInProgress$(),
            this._sessionCommunicationService.isSessionTicketPassbookContentImagesSaving$(),
            this._sessionCommunicationService.isSessionTicketPassbookContentImagesRemoving$()
        ]);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this._canChangeLanguage();

    changedLanguage(language: string): void {
        this._selectedLanguage.next(language);
    }

    reload(): void {
        if (this._passbookContentComponent) {
            this._passbookContentComponent.reload();
        }
        if (this._channelContentComponent) {
            this._channelContentComponent.reload();
        }
        if (this._ticketContentComponent) {
            this._ticketContentComponent.reload();
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reload());
    }

    save$(): Observable<(void | void[])[]> {
        if (this.form.valid) {
            const obs$: Observable<void | void[]>[] = [];
            if (this._passbookContentComponent) {
                obs$.push(...this._passbookContentComponent.save());
            }
            if (this._channelContentComponent) {
                obs$.push(...this._channelContentComponent.save());
            }
            if (this._ticketContentComponent) {
                obs$.push(...this._ticketContentComponent.save());
            }
            return forkJoin(obs$)
                .pipe(tap(() => this._ephemeralMessage.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    // eslint-disable-next-line @typescript-eslint/naming-convention
    private _canChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }

}
