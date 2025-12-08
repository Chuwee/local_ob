import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EventCommunicationService, EventTicketTemplateFields, EventTicketTemplate

} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventAvetConnection, EventSessionPackConf, EventsService, TicketContentImageFields,
    TicketContentImageRequest, TicketContentText, TicketContentTextFields
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketTemplatesService } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType, EventType } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, IconManagerService, MessageDialogService, starIcon, ticketPassbookIcon, ticketPdfIcon, ticketPrinterIcon
} from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { map, take, tap } from 'rxjs/operators';
import { EventTicketContentPassbookComponent } from './ticket-content-passbook/event-ticket-content-passbook.component';
import { EventTicketContentPdfComponent } from './ticket-content-pdf/event-ticket-content-pdf.component';
import { EventTicketContentPrinterComponent } from './ticket-content-printer/event-ticket-content-printer.component';
import { EventTicketTemplatesComponent } from './ticket-templates/event-ticket-templates.component';

@Component({
    selector: 'app-event-ticket-content',
    templateUrl: './event-ticket-content.component.html',
    styleUrls: ['./event-ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventTicketContentComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _selectedLanguage = new BehaviorSubject<string>(null);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('templatesComponent')
    private _templatesComponent: EventTicketTemplatesComponent;

    @ViewChild('pdfContent')
    private _pdfContentComponent: EventTicketContentPdfComponent;

    @ViewChild('printerContent')
    private _printerContentComponent: EventTicketContentPrinterComponent;

    @ViewChild('passbookContent')
    private _passbookContentComponent: EventTicketContentPassbookComponent;

    @Input() ticketType = TicketType.general;

    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    languages$: Observable<string[]>;
    passbookEnabled$: Observable<boolean>;
    seasonPackEnabled$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventsService: EventsService,
        private _eventCommunicationService: EventCommunicationService,
        private _messageDialogService: MessageDialogService,
        private _ephemeralMessage: EphemeralMessageService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _iconManagerSrv: IconManagerService
    ) {
        this._iconManagerSrv.addIconDefinition(ticketPdfIcon, ticketPrinterIcon, ticketPassbookIcon, starIcon);
    }

    ngOnInit(): void {
        // base form
        this.form = this._fb.group({});

        this.passbookEnabled$ = this._eventsService.event.get$()
            .pipe(
                map(event =>
                    !(
                        this.ticketType === TicketType.invitation ||
                        (event.type === EventType.avet && event.additional_config?.avet_config === EventAvetConnection.ws)
                    )
                )
            );

        this.seasonPackEnabled$ = this._eventsService.event.get$()
            .pipe(map(event => !(event.settings.session_pack === EventSessionPackConf.disabled)));

        this.languages$ = this._eventsService.event.get$()
            .pipe(
                take(1),
                map(event => event.settings.languages),
                tap(languages => this._selectedLanguage.next(languages.default)),
                map(languages => languages.selected)
            );

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._ticketTemplatesService.isTicketTemplatesLoading$(),
            this._eventCommunicationService.isEventTicketTemplatesLoading$(),
            this._eventCommunicationService.isEventTicketTemplatesSaving$(),
            this._eventCommunicationService.isEventTicketPdfContentTextsLoading$(),
            this._eventCommunicationService.isEventTicketPdfContentImagesLoading$(),
            this._eventCommunicationService.isEventTicketPdfContentTextsSaving$(),
            this._eventCommunicationService.isEventTicketPdfContentImagesSaving$(),
            this._eventCommunicationService.isEventTicketPdfContentImagesRemoving$(),
            this._eventCommunicationService.isEventTicketPrinterContentTextsLoading$(),
            this._eventCommunicationService.isEventTicketPrinterContentImagesLoading$(),
            this._eventCommunicationService.isEventTicketPrinterContentTextsSaving$(),
            this._eventCommunicationService.isEventTicketPrinterContentImagesSaving$(),
            this._eventCommunicationService.isEventTicketPrinterContentImagesRemoving$(),
            this._eventCommunicationService.isEventTicketPassbookContentImagesLoading$(),
            this._eventCommunicationService.isEventTicketPassbookContentImagesSaving$(),
            this._eventCommunicationService.isEventTicketPassbookContentImagesRemoving$(),
            this._eventCommunicationService.isTicketPdfPreviewDownloading$(),
            this._eventCommunicationService.isDownloadUrlPassbookPreviewInProgress$()
        ]);
    }

    ngOnDestroy(): void {
        this._eventCommunicationService.cancelRequests();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    canChangeLanguage: () => Observable<boolean> = () =>
        this.validateIfCanChangeLanguage();

    cancel(): void {
        if (this._templatesComponent) {
            this._templatesComponent.cancel();
        }
        if (this._pdfContentComponent) {
            this._pdfContentComponent.cancel();
        }
        if (this._printerContentComponent) {
            this._printerContentComponent.cancel();
        }
        if (this._passbookContentComponent) {
            this._passbookContentComponent.cancel();
        }
    }

    getTemplateFields(contentForm: UntypedFormGroup, templateFields: EventTicketTemplateFields[]): EventTicketTemplate[] {
        const templatesToSave: EventTicketTemplate[] = [];
        templateFields.forEach(templateField => {
            const field = contentForm.get(templateField.formField);
            if (field.dirty) {
                templatesToSave.push({ id: field.value, type: templateField.type, format: templateField.format });
            }
        });
        return templatesToSave;
    }

    getTextFields(contentForm: UntypedFormGroup, textFields: TicketContentTextFields[], language: string): TicketContentText[] {
        const textsToSave: TicketContentText[] = [];
        textFields.forEach(textField => {
            const field = contentForm.get(textField.type);
            if (field.dirty) {
                textsToSave.push({ type: textField.type, value: field.value, language });
            }
        });
        return textsToSave;
    }

    getImageFields(
        contentForm: UntypedFormGroup, imageFields: TicketContentImageFields[], language: string
    ): { [key: string]: TicketContentImageRequest[] } {
        const imagesToSave: TicketContentImageRequest[] = [];
        const imagesToDelete: TicketContentImageRequest[] = [];
        imageFields.forEach(imageField => {
            const field = contentForm.get(imageField.type);
            if (field.dirty) {
                const imageValue = field.value;
                if (imageValue?.data) {
                    imagesToSave.push({ type: imageField.type, image: imageValue?.data, language });
                } else {
                    imagesToDelete.push({ type: imageField.type, image: null, language });
                }
            }
        });
        return { imagesToSave, imagesToDelete };
    }

    save(): void {
        this.save$()?.subscribe();
    }

    save$(): Observable<(void | void[])[]> {
        if (this.form.valid) {
            const obs$: Observable<void | void[]>[] = [];
            if (this._templatesComponent) {
                obs$.push(...this._templatesComponent.save(this.getTemplateFields.bind(this)));
            }
            if (this._pdfContentComponent) {
                obs$.push(...this._pdfContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            if (this._printerContentComponent) {
                obs$.push(...this._printerContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            if (this._passbookContentComponent) {
                obs$.push(...this._passbookContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            return forkJoin(obs$).pipe(tap(() => this._ephemeralMessage.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    changeLanguage(newLanguage: string): void {
        this._selectedLanguage.next(newLanguage);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
