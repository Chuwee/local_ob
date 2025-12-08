import {
    TicketContentFieldsRestrictions, eventTicketImageRestrictions, EventCommunicationService
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventsService, TicketContentImageType, TicketContentImage, TicketContentImageFields, TicketContentImageRequest,
    TicketContentTextType, TicketContentText, TicketContentTextFields
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketTemplate, TicketTemplatesService, TicketTemplateFormat } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';
import { PRINTER_TEXT_FIELDS } from '../../price-type-ticket-content/models/ticket-content-printer';

@Component({
    selector: 'app-event-ticket-content-printer',
    templateUrl: './event-ticket-content-printer.component.html',
    styleUrls: ['./event-ticket-content-printer.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventTicketContentPrinterComponent
    implements OnInit, OnDestroy, AfterViewInit {
    private readonly _onDestroy = new Subject<void>();
    private readonly _imageFields = [
        { type: TicketContentImageType.body },
        { type: TicketContentImageType.bannerMain }
    ];

    private _eventId: number;
    private _images$: Observable<TicketContentImage[]>;
    private _texts$: Observable<TicketContentText[]>;
    private _selectedLanguage: string;

    readonly textType = TicketContentTextType;
    readonly imageType = TicketContentImageType;
    readonly textRestrictions = TicketContentFieldsRestrictions;
    readonly imageRestrictions = eventTicketImageRestrictions;
    printerContentExpanded: boolean;
    printerContentForm: UntypedFormGroup;
    printerTemplateSelected$: Observable<TicketTemplate>;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() ticketType = TicketType.general;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventService: EventsService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _eventCommunicationService: EventCommunicationService
    ) { }

    ngOnInit(): void {
        this.initForms();
        this.loadContents();
        this.language$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(lang => (this._selectedLanguage = lang));
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.printerContentForm.markAsPristine();
        this._eventCommunicationService.loadEventTicketPrinterContentTexts(this.ticketType, this._eventId);
        this._eventCommunicationService.loadEventTicketPrinterContentImages(this.ticketType, this._eventId);
    }

    save(
        getTextFields: (contentForm: UntypedFormGroup, textFields: TicketContentTextFields[], language: string) => TicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: TicketContentImageFields[], language: string
        ) => { [key: string]: TicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(this.printerContentForm, PRINTER_TEXT_FIELDS, this._selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(
                this._eventCommunicationService.saveEventTicketPrinterContentTexts(this.ticketType, this._eventId, textsToSave)
            );
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.printerContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this._eventCommunicationService.saveEventTicketPrinterContentImages(this.ticketType, this._eventId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this._eventCommunicationService.deleteEventTicketPrinterContentImages(this.ticketType, this._eventId, imagesToDelete)
            );
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1]
                .pipe(
                    tap(() => this.cancel()) // reloads data from backend
                );
        }
        return obsToSave$;
    }

    private initForms(): void {
        const printerFields = {};

        PRINTER_TEXT_FIELDS.forEach(textField => (printerFields[textField.type] = [null, textField.validators]));
        this._imageFields.forEach(imageField => (printerFields[imageField.type] = null));

        this.printerContentForm = this._fb.group(printerFields);
        this.form.addControl('printerContent', this.printerContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._eventService.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._eventId = event.id;
                    this._eventCommunicationService.loadEventTicketPrinterContentTexts(this.ticketType, this._eventId);
                    this._eventCommunicationService.loadEventTicketPrinterContentImages(this.ticketType, this._eventId);
                })
            ).subscribe();

        this._texts$ = combineLatest([
            this._eventCommunicationService.getEventTicketPrinterContentTexts$(),
            this.language$
        ]).pipe(
            filter(([printerTexts, language]) => !!printerTexts && !!language),
            tap(([printerTexts, language]) => {
                PRINTER_TEXT_FIELDS.forEach(textField => {
                    const field = this.printerContentForm.get(textField.type);
                    field.reset();
                    for (const text of printerTexts) {
                        if (text.language === language && text.type === textField.type) {
                            field.setValue(text.value);
                            return;
                        }
                    }
                });
            }),
            map(([texts, _]) => texts)
        );

        this._images$ = combineLatest([
            this._eventCommunicationService.getEventTicketPrinterContentImages$(),
            this.language$
        ]).pipe(
            filter(([printerImages, language]) => !!printerImages && !!language),
            map(([printerImages, language]) => printerImages.filter(img => img.language === language)),
            tap(printerImages => {
                this._imageFields.forEach(imageField => {
                    const field = this.printerContentForm.get(imageField.type);
                    field.reset();
                    for (const image of printerImages) {
                        if (image.type === imageField.type) {
                            field.setValue(image.image_url);
                            return;
                        }
                    }
                });
            })
        );

        this.printerTemplateSelected$ = combineLatest([
            this.form.get('templatesForm.singlePrinterTemplate').valueChanges,
            this._ticketTemplatesService.getTicketTemplates$()
                .pipe(filter(templates => !!templates))
        ]).pipe(
            map(([templateId, templates]) => templates.filter(template =>
                template.design.format === TicketTemplateFormat.printer && template.id === templateId)[0]
            )
        );
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._texts$,
            this._images$,
            this.form.valueChanges
        ])
            .pipe(
                filter(([language, texts, images]) => !!language && !!texts && !!images),
                takeUntil(this._onDestroy)
            )
            .subscribe(([, , images]) => {
                this._imageFields.forEach(imageField => {
                    const field = this.printerContentForm.get(imageField.type);
                    const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                    FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                });
            });
    }
}
