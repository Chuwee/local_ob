import {
    eventTicketImageRestrictions, EventCommunicationService, TicketContentFormat, EventTicketTemplateType,
    TicketContentFieldsRestrictions as TextRestrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventsService, TicketContentImageType as ImageType, TicketContentImage, TicketContentTextType as TextType, TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import {
    TicketTemplate, TicketTemplatesService, TicketTemplateFormat
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, startWith, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { PDF_IMAGE_FIELDS, PDF_TEXT_FIELDS } from '../models/ticket-content-pdf';

const GROUP_NAME = 'pdf';

@Component({
    selector: 'app-price-type-ticket-content-pdf',
    templateUrl: './price-type-ticket-content-pdf.component.html',
    styleUrls: ['./price-type-ticket-content-pdf.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PriceTypeTicketContentPdfComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    textType = TextType;
    imageType = ImageType;
    pdfContentExpanded: boolean;
    textRestrictions = TextRestrictions;
    imageRestrictions = eventTicketImageRestrictions;
    parentForm: UntypedFormGroup;
    pdfContentForm: UntypedFormGroup;
    pdfTemplateSelected$: Observable<TicketTemplate>;
    placeholders$: Observable<{
        texts?: Record<string, string>;
        images?: Record<string, string>;
    }>;

    @Input() set form(form: UntypedFormGroup) {
        this.parentForm = form;
        this.initForms();
    }

    @Input() language$: Observable<string>;
    @Input() images$: Observable<TicketContentImage[]>;
    @Input() texts$: Observable<TicketContentText[]>;
    @Input() ticketType = TicketType.general;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventService: EventsService,
        private _eventCommunicationService: EventCommunicationService,
        private _ticketTemplatesService: TicketTemplatesService
    ) { }

    ngOnInit(): void {
        this._eventService.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._eventCommunicationService.loadEventTicketPdfContentTexts(this.ticketType, event.id);
                    this._eventCommunicationService.loadEventTicketPdfContentImages(this.ticketType, event.id);
                })
            ).subscribe();

        this.placeholders$ = combineLatest([
            this.language$,
            this._eventCommunicationService.getEventTicketPdfContentTexts$(),
            this._eventCommunicationService.getEventTicketPdfContentImages$()
        ]).pipe(
            filter(([lang, texts, images]) => !!lang && !!texts && !!images),
            map(([lang, texts, images]) => ({
                texts: texts
                    .filter(elem => elem.language === lang)
                    .reduce((acc, curr) => ((acc[curr.type] = curr.value), acc), {}),
                images: images
                    .filter(elem => elem.language === lang)
                    .reduce((acc, curr) => ((acc[curr.type] = curr.image_url), acc), {})
            })),
            startWith({})
        );

        this.pdfTemplateSelected$ = combineLatest([
            this._eventCommunicationService.getEventTicketTemplates$(),
            this._ticketTemplatesService.getTicketTemplates$()
        ]).pipe(
            filter(([eventTemplates, entityTemplates]) => !!entityTemplates && !!eventTemplates),
            map(([eventTemplates, entityTemplates]) => {
                const pdfTemplate = eventTemplates?.find(template =>
                    template.format === TicketContentFormat.pdf &&
                    template.type === EventTicketTemplateType.single
                );
                return (
                    pdfTemplate &&
                    entityTemplates.filter(template =>
                        template.design.format === TicketTemplateFormat.pdf &&
                        template.id === (isNaN(+pdfTemplate.id) ? pdfTemplate.id : +pdfTemplate.id)
                    )?.[0]
                );
            })
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private initForms(): void {
        if (!this.parentForm?.get(GROUP_NAME)) {
            const fields = {};

            PDF_TEXT_FIELDS.forEach(textField => (fields[textField.type] = [null, textField.validators]));
            PDF_IMAGE_FIELDS.forEach(imageField => (fields[imageField.type] = null));

            this.pdfContentForm = this._fb.group(fields);

            if (this.parentForm) {
                this.pdfContentForm.enable();
                this.parentForm.addControl(GROUP_NAME, this.pdfContentForm);
                this.loadContents();
                this.refreshFormDataHandler();
            } else {
                this.pdfContentForm.disable();
            }
        } else {
            this.pdfContentForm = this.parentForm?.get(GROUP_NAME) as UntypedFormGroup;
        }
    }

    private loadContents(): void {
        this.texts$
            .pipe(
                first(elem => !!elem),
                tap(texts => {
                    texts?.length &&
                        PDF_TEXT_FIELDS.forEach(textField => {
                            const field = this.pdfContentForm.get(textField.type);
                            const text = texts.find(text => text.type === textField.type);
                            field.reset(text?.value);
                        });
                })
            ).subscribe();

        this.images$
            .pipe(
                first(elem => !!elem),
                takeUntil(this._onDestroy),
                tap(images => {
                    PDF_IMAGE_FIELDS.forEach(imageField => {
                        const field = this.pdfContentForm.get(imageField.type);
                        const image = images?.find(image => image.type === imageField.type);
                        const value = image?.image_url;
                        field.reset(value);
                    });
                })
            ).subscribe();
    }

    private refreshFormDataHandler(): void {
        this.pdfContentForm.valueChanges
            .pipe(
                withLatestFrom(this.texts$, this.images$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([, texts, images]) => {
                images?.length &&
                    PDF_IMAGE_FIELDS.forEach(imageField => {
                        const field = this.pdfContentForm.get(imageField.type);
                        const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                        FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                    });
                texts?.length &&
                    PDF_TEXT_FIELDS.forEach(textfield => {
                        const field = this.pdfContentForm.get(textfield.type);
                        const originalValue = texts.filter(text => text.type === textfield.type)[0]?.value || '';
                        FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                    });
            });
    }
}
