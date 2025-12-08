import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventAvetConnection, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { SessionCommunicationService } from '@admin-clients/cpanel-promoters-events-sessions-communication-data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { MessageDialogService, EphemeralMessageService, LanguageBarComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChildren, QueryList, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';
import { SessionChannelContentComponent } from './session-channel-content/session-channel-content.component';
import { SessionCommunicationContentModule } from './session-communication-content.module';
import { SessionTicketContentComponent } from './session-ticket-content/session-ticket-content.component';
import { SessionTicketPassbookComponent } from './session-ticket-passbook/session-ticket-passbook.component';

@Component({
    selector: 'app-session-communication',
    templateUrl: './session-communication.component.html',
    styleUrls: ['./session-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        LanguageBarComponent,
        CommonModule,
        FlexLayoutModule,
        SessionCommunicationContentModule,
        MaterialModule,
        ArchivedEventMgrComponent
    ]
})
export class SessionCommunicationComponent implements WritingComponent, OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _selectedLanguage = new BehaviorSubject<string>(null);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('channelContent')
    private _channelContentComponent: SessionChannelContentComponent;

    @ViewChild('ticketContent')
    private _ticketContentComponent: SessionTicketContentComponent;

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

    canChangeLanguage: (() => Observable<boolean>) = () => {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    };

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
        this.save$().subscribe(() => {
            this.reload();
        });
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
                .pipe(
                    tap(() => this._ephemeralMessage.showSaveSuccess())
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }
}
