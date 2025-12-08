import {
    EventCommunicationService, EventTicketTemplateType, TicketContentFormat
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, SessionType, SessionWrapper } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    SessionTicketContentTextFields, SessionTicketContentImageFields, SessionTicketTextRestrictions, sessionTicketPassbookBodyRestrictions,
    SessionCommunicationService, sessionTicketContetPassbookFieldsRelations, SessionTicketTextType, SessionTicketPassbookImageType,
    SessionTicketText, SessionTicketImage, SessionTicketImageRequest
} from '@admin-clients/cpanel-promoters-events-sessions-communication-data-access';
import {
    TicketPassbook, TicketsPassbookService
} from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { IconManagerService, ticketPassbookIcon } from '@admin-clients/shared/common/ui/components';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, combineLatest, iif, Observable, of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-ticket-passbook',
    templateUrl: './session-ticket-passbook.component.html',
    styleUrls: ['./session-ticket-passbook.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionTicketPassbookComponent
    implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();
    private _textFields: SessionTicketContentTextFields[];
    private _imageField: SessionTicketContentImageFields;
    private _eventId: number;
    private _sessionId: number;
    private _entityId: number;
    private _defaultTicketPassbookName = new BehaviorSubject<string>(null);
    private _selectedLanguage: string;
    private _ticketPassbook$: Observable<TicketPassbook>;
    private _isSessionPacks: boolean;
    private _selectedSessions: SessionWrapper[];

    sessionName: string;
    passbookContentExpanded: boolean;
    defaultTicketPassbookName$: Observable<string> = this._defaultTicketPassbookName.asObservable();

    restrictions = SessionTicketTextRestrictions;
    imageRestrictions = sessionTicketPassbookBodyRestrictions;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() multiple: boolean;

    get passbookContentForm(): UntypedFormGroup {
        return this.form.get('passbookContentForm') as UntypedFormGroup;
    }

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventCommunicationService: EventCommunicationService,
        private _eventService: EventsService,
        private _sessionService: EventSessionsService,
        private _ticketsPassbookService: TicketsPassbookService,
        private _sessionCommunicationService: SessionCommunicationService,
        private _iconManagerSrv: IconManagerService
    ) {
        this._iconManagerSrv.addIconDefinition(ticketPassbookIcon);
    }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();

        combineLatest([
            this._eventService.event.get$(),
            this._sessionService.session.get$(),
            iif(() => this.multiple, this._sessionService.getSelectedSessions$(), of([] as SessionWrapper[]))
        ])
            .pipe(
                filter(([event]) => !!event),
                takeUntil(this._onDestroy)
            )
            .subscribe(([event, session, sessions]) => {
                this._eventId = event.id;
                this._entityId = event.entity.id;
                if (this.multiple) {
                    this.clearPassbookContent();
                    this._selectedSessions = sessions;
                    this.loadContents();
                } else if (session) {
                    this.sessionName = session.name;
                    this._sessionId = session.id;
                    this._isSessionPacks = session.type.includes(SessionType.restrictedPack || SessionType.unrestrictedPack);
                    this.loadContents();
                }
            });

        this.language$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(lang => (this._selectedLanguage = lang));

        combineLatest([
            this._eventCommunicationService.getEventTicketTemplates$(),
            this._ticketsPassbookService.getTicketPassbookListData$()
        ])
            .pipe(first(([templates, templatesPassbook]) => !!templates && !!templatesPassbook))
            .subscribe(([templates, ticketsPassbook]) => {
                const passbookTemplateId = templates.find(template =>
                    template.format === TicketContentFormat.passbook &&
                    template.type === (this._isSessionPacks ? EventTicketTemplateType.seasonPack : EventTicketTemplateType.single)
                )?.id;
                const selectedTicketPassbook = ticketsPassbook.find(ticket =>
                    passbookTemplateId ? ticket.code === String(passbookTemplateId) : ticket.default_passbook
                );
                this._defaultTicketPassbookName.next(selectedTicketPassbook.name);
                this._ticketsPassbookService.loadTicketPassbook(selectedTicketPassbook.code, String(this._entityId));
            });

        this._ticketPassbook$ = this._ticketsPassbookService.getTicketPassbook$()
            .pipe(
                filter(ticketPassbook => !!ticketPassbook),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
        this.formChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this.clearPassbookContent();
    }

    reload(reload = true): void {
        this.passbookContentForm.markAsPristine();
        this.loadContents();
        if (this.multiple && reload) {
            this.clearPassbookContent();
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

        if (this.passbookContentForm.valid) {
            obsToSave$.push(...this.saveTexts(sessionIds, this._selectedLanguage));
            obsToSave$.push(...this.saveOrDeleteImages(sessionIds));
        }
        return obsToSave$;
    }

    downloadPassbook(): void {
        this._sessionCommunicationService.getDownloadUrlPassbookPreview$(this._eventId, this._sessionId)
            .pipe(first())
            .subscribe(resp => window.open(resp.download_url, '_blank'));
    }

    isPassbookFieldActive$(field?: string): Observable<boolean> {
        return this._ticketPassbook$.pipe(
            filter(ticketPasbook => !!ticketPasbook),
            map(ticketPassbook => {
                const ticketPassbookValues = [].concat(...Object.values(ticketPassbook));
                if (field) {
                    // Check if X field exist in passbook
                    const passbookFields = Object.entries(sessionTicketContetPassbookFieldsRelations)
                        .filter(([_, value]) => value === field)
                        .map(([key]) => key);
                    return !ticketPassbookValues.some(properties =>
                        passbookFields.includes(properties?.key) && field === sessionTicketContetPassbookFieldsRelations[properties?.key]
                    );
                } else {
                    // Check if any field exist in passbook
                    const passbookFieldKeys = Object.keys(sessionTicketContetPassbookFieldsRelations);
                    const filteredField = ticketPassbookValues
                        .filter(properties => properties?.key)
                        .map(({ key }) => key);
                    return !passbookFieldKeys.some(key => filteredField.includes(key));
                }
            })
        );
    }

    private prepareFields(): void {
        this._textFields = [
            {
                formField: 'title',
                type: SessionTicketTextType.title,
                maxLength: SessionTicketTextRestrictions.titlePassbookLength
            },
            {
                formField: 'additionalData1',
                type: SessionTicketTextType.additionalData1,
                maxLength: SessionTicketTextRestrictions.additionalDataPassbookLength
            },
            {
                formField: 'additionalData2',
                type: SessionTicketTextType.additionalData2,
                maxLength: SessionTicketTextRestrictions.additionalDataPassbookLength
            },
            {
                formField: 'additionalData3',
                type: SessionTicketTextType.additionalData3,
                maxLength: SessionTicketTextRestrictions.additionalDataPassbookLength
            }
        ];
        this._imageField = {
            formField: 'strip',
            type: SessionTicketPassbookImageType.strip,
            maxSize: sessionTicketPassbookBodyRestrictions.size
        };
    }

    private initForms(): void {
        const passbookFields = {};

        this._textFields.forEach(textField => {
            passbookFields[textField.formField] = [null, [Validators.maxLength(textField.maxLength)]];
        });
        passbookFields[this._imageField.formField] = null;

        this.form.addControl('passbookContentForm', this._fb.group(passbookFields));
    }

    private loadContents(): void {
        if (!this.multiple) {
            this._sessionCommunicationService.loadSessionTicketPassbookContentTexts(this._eventId, this._sessionId);
            this._sessionCommunicationService.loadSessionTicketPassbookContentImages(this._eventId, this._sessionId);
        }
        this._eventCommunicationService.loadEventTicketTemplates(this._eventId);
        this._ticketsPassbookService.loadTicketPassbookList({
            limit: 999,
            offset: 0,
            entity_id: this._entityId
        });
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._sessionCommunicationService.getSessionTicketPassbookContentTexts$(),
            this._sessionCommunicationService.getSessionTicketPassbookContentImages$()
        ])
            .pipe(
                takeUntil(this._onDestroy),
                tap(() => this.passbookContentForm.reset()),
                filter(data => data.every(item => !!item))
            )
            .subscribe(([language, passbookTexts, passbookImages]) => {
                this.applyOnFields(language, passbookTexts, passbookImages, (field, value) => field.setValue(value));
            });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.language$,
            this._sessionCommunicationService.getSessionTicketPassbookContentTexts$(),
            this._sessionCommunicationService.getSessionTicketPassbookContentImages$(),
            this.form.valueChanges
        ])
            .pipe(
                takeUntil(this._onDestroy),
                filter(data => data.every(item => !!item))
            )
            .subscribe(([language, passbookTexts, passbookImages]) => {
                this.applyOnFields(
                    language, passbookTexts, passbookImages, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value)
                );
            });
    }

    private clearPassbookContent(): void {
        this.passbookContentForm.reset();
        this._sessionCommunicationService.clearSessionTicketPassbookContentTexts();
        this._sessionCommunicationService.clearSessionTicketPassbookContentImages();
        this._ticketsPassbookService.clearTicketPassbook();
    }

    private applyOnFields(
        language: string, passbookTexts: SessionTicketText[], passbookImages: SessionTicketImage[],
        doOnField: (field: AbstractControl, value: string) => void
    ): void {
        this.applyOnTextFields(language, passbookTexts, doOnField);
        this.applyOnImageFields(language, passbookImages, doOnField);
    }

    private applyOnTextFields(
        language: string, texts: SessionTicketText[], doOnField: (field: AbstractControl, value: string) => void
    ): void {
        this._textFields.forEach(textField => {
            const field = this.getField(textField.formField);
            for (const text of texts) {
                if (text.language === language && text.type === textField.type) {
                    doOnField(field, text.value);
                    return;
                }
            }
            doOnField(field, null);
        });
    }

    private applyOnImageFields(
        language: string, images: SessionTicketImage[],
        doOnField: (field: AbstractControl, value: string) => void
    ): void {
        const field = this.getField(this._imageField.formField);
        for (const image of images) {
            if (image.language === language && image.type === this._imageField.type) {
                doOnField(field, image.image_url);
                return;
            }
        }
        doOnField(field, null);
    }

    private getField(formField: string): AbstractControl {
        return this.passbookContentForm.get(formField);
    }

    private saveTexts(sessionIds: number[], language: string): Observable<void>[] {
        const operations$ = [];
        const textsToSave = [];
        const lang = language;
        this._textFields.forEach(textField => {
            const field = this.getField(textField.formField);
            if (field.dirty) {
                textsToSave.push({ language: lang, type: textField.type, value: field.value });
            }
        });

        if (textsToSave.length > 0) {
            operations$.push(
                this._sessionCommunicationService.saveSessionTicketPassbookContentTexts(this._eventId, sessionIds, textsToSave)
            );
        }
        return operations$;
    }

    private saveOrDeleteImages(sessionIds: number[]): Observable<void | void[]>[] {
        const images = [];
        const imagesToDelete = [];
        const lang = this._selectedLanguage;

        const field = this.getField(this._imageField.formField);
        if (field.dirty) {
            const image = { language: lang, type: this._imageField.type } as SessionTicketImageRequest;
            if (field.value) {
                image.image = field.value.data;
                images.push(image);
            } else {
                imagesToDelete.push(image);
            }
        }

        const operations = [] as Observable<void | void[]>[];
        if (images.length > 0) {
            operations.push(
                this._sessionCommunicationService.saveSessionTicketPassbookContentImages(this._eventId, sessionIds, images)
            );
        }
        if (imagesToDelete.length > 0) {
            operations.push(
                this._sessionCommunicationService.deleteSessionTicketPassbookContentImages(this._eventId, sessionIds, imagesToDelete)
            );
        }
        return operations;
    }
}
