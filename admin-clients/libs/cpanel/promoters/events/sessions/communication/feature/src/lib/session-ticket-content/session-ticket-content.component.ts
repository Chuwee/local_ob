import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, SessionWrapper } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    sessionTicketPdfBodyRestrictions, sessionTicketPrinterBodyRestrictions, SessionTicketTextRestrictions,
    SessionCommunicationService, SessionTicketTextType, SessionTicketPdfImageType, SessionTicketPrinterImageType
} from '@admin-clients/cpanel-promoters-events-sessions-communication-data-access';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, iif, Observable, of, Subject } from 'rxjs';
import { filter, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-ticket-content',
    templateUrl: './session-ticket-content.component.html',
    styleUrls: ['./session-ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionTicketContentComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();
    private _selectedLanguage: string;
    private _eventId: number;
    private _sessionId: number;
    private _selectedSessions: SessionWrapper[];

    sessionName: string;
    pdfBodyImageRestrictions = sessionTicketPdfBodyRestrictions;
    printerBodyImageRestrictions = sessionTicketPrinterBodyRestrictions;
    textRestrictions = SessionTicketTextRestrictions;
    ticketContentExpanded: boolean;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() multiple: boolean;

    get ticketContentForm(): UntypedFormGroup {
        return this.form.get('ticketContentForm') as UntypedFormGroup;
    }

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventService: EventsService,
        private _sessionService: EventSessionsService,
        private _sessionCommunicationService: SessionCommunicationService
    ) { }

    ngOnInit(): void {
        this.initForms();
        this.loadContents();
        this.language$.pipe(takeUntil(this._onDestroy)).subscribe(lang => this._selectedLanguage = lang);
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this.clearTicketContent();
    }

    reload(reload = true): void {
        this.ticketContentForm.markAsPristine();
        if (!this.multiple) {
            this._sessionCommunicationService.loadTicketPdfTexts(this._eventId, this._sessionId);
            this._sessionCommunicationService.loadTicketPrinterTexts(this._eventId, this._sessionId);
            this._sessionCommunicationService.loadTicketPdfImages(this._eventId, this._sessionId);
            this._sessionCommunicationService.loadTicketPrinterImages(this._eventId, this._sessionId);
        } else if (this.multiple && reload) {
            this.clearTicketContent();
        }
    }

    save(): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        let sessionIds: number[];
        if (this.multiple) {
            sessionIds = this._selectedSessions.map(session => session.session.id);
        } else {
            sessionIds = [this._sessionId];
        }

        if (this.ticketContentForm.get('pdfTitle').dirty) {
            const textValue = this.ticketContentForm.get('pdfTitle').value;
            obsToSave$.push(this._sessionCommunicationService.updateTicketPdfTexts(
                this._eventId, sessionIds, [{ type: SessionTicketTextType.title, value: textValue, language: this._selectedLanguage }]));
        }

        if (this.ticketContentForm.get('pdfBody').dirty) {
            const imageValue = this.ticketContentForm.get('pdfBody').value;
            if (imageValue?.data) {
                obsToSave$.push(this._sessionCommunicationService.updateTicketPdfImages(this._eventId, sessionIds,
                    [{ type: SessionTicketPdfImageType.body, image: imageValue.data, language: this._selectedLanguage }]));
            } else {
                obsToSave$.push(this._sessionCommunicationService.deleteTicketPdfImages(this._eventId, sessionIds,
                    [{ type: SessionTicketPdfImageType.body, image: null, language: this._selectedLanguage }]));
            }
        }

        if (this.multiple && this.ticketContentForm.get('setDefaultPdfBody').dirty) {
            obsToSave$.push(this._sessionCommunicationService.setDefaultTicketPdfImages(
                this._eventId, sessionIds, this._selectedLanguage));
        }

        if (this.ticketContentForm.get('printerTitle').dirty) {
            const textValue = this.ticketContentForm.get('printerTitle').value;
            obsToSave$.push(this._sessionCommunicationService.updateTicketPrinterTexts(
                this._eventId, sessionIds, [{ type: SessionTicketTextType.title, value: textValue, language: this._selectedLanguage }]));
        }

        if (this.ticketContentForm.get('printerBody').dirty) {
            const imageValue = this.ticketContentForm.get('printerBody').value;
            if (imageValue?.data) {
                obsToSave$.push(this._sessionCommunicationService.updateTicketPrinterImages(this._eventId, sessionIds,
                    [{ type: SessionTicketPrinterImageType.body, image: imageValue.data, language: this._selectedLanguage }]));
            } else {
                obsToSave$.push(this._sessionCommunicationService.deleteTicketPrinterImages(this._eventId, sessionIds,
                    [{ type: SessionTicketPrinterImageType.body, image: null, language: this._selectedLanguage }]));
            }
        }

        if (this.multiple && this.ticketContentForm.get('setDefaultPrinterBody').dirty) {
            obsToSave$.push(this._sessionCommunicationService.setDefaultTicketPrinterImages(
                this._eventId, sessionIds, this._selectedLanguage));
        }

        return obsToSave$;
    }

    private initForms(): void {
        this.form.addControl('ticketContentForm', this._fb.group({
            pdfTitle: [null, Validators.maxLength(this.textRestrictions.titleMaxLength)],
            pdfBody: null,
            setDefaultPdfBody: { value: null, disabled: !this.multiple },
            printerTitle: [null, Validators.maxLength(this.textRestrictions.titleMaxLength)],
            printerBody: null,
            setDefaultPrinterBody: { value: null, disabled: !this.multiple }
        }));
    }

    private loadContents(): void {
        // load texts and images
        combineLatest([
            this._eventService.event.get$(),
            this._sessionService.session.get$(),
            iif(
                () => this.multiple,
                this._sessionService.getSelectedSessions$(),
                of([] as SessionWrapper[])
            )
        ])
            .pipe(
                filter(([event]) => !!event),
                takeUntil(this._onDestroy)
            )
            .subscribe(([event, session, sessions]) => {
                this._eventId = event.id;
                if (this.multiple) {
                    this.clearTicketContent();
                    this._selectedSessions = sessions;
                } else if (session) {
                    this.sessionName = session.name;
                    this._sessionId = session.id;
                    this._sessionCommunicationService.loadTicketPdfTexts(event.id, session.id);
                    this._sessionCommunicationService.loadTicketPrinterTexts(event.id, session.id);
                    this._sessionCommunicationService.loadTicketPdfImages(event.id, session.id);
                    this._sessionCommunicationService.loadTicketPrinterImages(event.id, session.id);
                }
            });

        combineLatest([
            this._sessionCommunicationService.getTicketPdfTexts$(),
            this._sessionCommunicationService.getTicketPrinterTexts$(),
            this.language$
        ])
            .pipe(
                tap(() => {
                    this.ticketContentForm.get('pdfTitle').reset();
                    this.ticketContentForm.get('printerTitle').reset();
                }),
                filter(([pdfTexts, printerTexts, language]) => !!pdfTexts && !!printerTexts && !!language),
                takeUntil(this._onDestroy)
            )
            .subscribe(([pdfTexts, printerTexts, language]) => {
                pdfTexts.filter(text => text.language === language && text.type === SessionTicketTextType.title)
                    .forEach(text => this.ticketContentForm.get('pdfTitle').reset(text.value));
                printerTexts.filter(text => text.language === language && text.type === SessionTicketTextType.title)
                    .forEach(text => this.ticketContentForm.get('printerTitle').reset(text.value));
            });

        combineLatest([
            this._sessionCommunicationService.getTicketPdfImages$(),
            this._sessionCommunicationService.getTicketPrinterImages$(),
            this.language$
        ])
            .pipe(
                tap(() => {
                    this.ticketContentForm.get('pdfBody').reset();
                    this.ticketContentForm.get('printerBody').reset();
                }),
                filter(([pdfImages, printerImages, language]) => !!pdfImages && !!printerImages && !!language),
                takeUntil(this._onDestroy)
            )
            .subscribe(([pdfImages, printerImages, language]) => {
                pdfImages.filter(image => image.language === language && image.type === SessionTicketPdfImageType.body)
                    .forEach(image => this.ticketContentForm.get('pdfBody').reset(image.image_url));
                printerImages.filter(image => image.language === language && image.type === SessionTicketPrinterImageType.body)
                    .forEach(image => this.ticketContentForm.get('printerBody').reset(image.image_url));
            });
    }

    private refreshFormDataHandler(): void {
        if (this.multiple) {
            this.ticketContentForm.get('setDefaultPdfBody').valueChanges
                .pipe(takeUntil(this._onDestroy))
                .subscribe(setDefault => {
                    if (setDefault) {
                        this.ticketContentForm.get('pdfBody').disable();
                    } else {
                        this.ticketContentForm.get('pdfBody').enable();
                    }
                    this.ticketContentForm.get('pdfBody').reset();
                });

            this.ticketContentForm.get('setDefaultPrinterBody').valueChanges
                .pipe(takeUntil(this._onDestroy))
                .subscribe(setDefault => {
                    if (setDefault) {
                        this.ticketContentForm.get('printerBody').disable();
                    } else {
                        this.ticketContentForm.get('printerBody').enable();
                    }
                    this.ticketContentForm.get('printerBody').reset();
                });
        }
    }

    private clearTicketContent(): void {
        this.ticketContentForm.reset();
        this._sessionCommunicationService.clearTicketPdfTexts();
        this._sessionCommunicationService.clearTicketPrinterTexts();
        this._sessionCommunicationService.clearTicketPdfImages();
        this._sessionCommunicationService.clearTicketPrinterImages();
    }
}
