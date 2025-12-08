import {
    eventTicketImageRestrictions, EventCommunicationService, TicketContentFormat, EventTicketTemplateType
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventsService, TicketContentImageType, TicketContentTextType, TicketContentImage, TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import {
    TicketTemplate, TicketTemplatesService, TicketTemplateFormat
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, filter, first, map, Observable, startWith, Subject, take, takeUntil, tap, withLatestFrom } from 'rxjs';
import { PRINTER_IMAGE_FIELDS, PRINTER_TEXT_FIELDS } from '../models/ticket-content-printer';

const GROUP_NAME = 'printer';

@Component({
    selector: 'app-price-type-ticket-content-printer',
    templateUrl: './price-type-ticket-content-printer.component.html',
    styleUrls: ['./price-type-ticket-content-printer.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PriceTypeTicketContentPrinterComponent
    implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    readonly textType = TicketContentTextType;
    readonly imageType = TicketContentImageType;
    readonly imageRestrictions = eventTicketImageRestrictions;
    expanded: boolean;
    parentForm: UntypedFormGroup;
    contentForm: UntypedFormGroup;
    templateSelected$: Observable<TicketTemplate>;
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
        private _ticketTemplatesService: TicketTemplatesService,
        private _eventCommunicationService: EventCommunicationService
    ) { }

    ngOnInit(): void {
        this._eventService.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._eventCommunicationService.loadEventTicketPrinterContentTexts(this.ticketType, event.id);
                    this._eventCommunicationService.loadEventTicketPrinterContentImages(this.ticketType, event.id);
                })
            ).subscribe();

        this.placeholders$ = combineLatest([
            this.language$,
            this._eventCommunicationService.getEventTicketPrinterContentTexts$(),
            this._eventCommunicationService.getEventTicketPrinterContentImages$()
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

        this.templateSelected$ = combineLatest([
            this._eventCommunicationService.getEventTicketTemplates$(),
            this._ticketTemplatesService.getTicketTemplates$()
        ]).pipe(
            filter(([eventTemplates, entityTemplates]) => !!entityTemplates && !!eventTemplates),
            map(([eventTemplates, entityTemplates]) => {
                const eventTemplate = eventTemplates?.find(template =>
                    template.format === TicketContentFormat.printer &&
                    template.type === EventTicketTemplateType.single
                );
                return (
                    eventTemplate &&
                    entityTemplates.filter(template =>
                        template.design.format === TicketTemplateFormat.printer &&
                        template.id === (isNaN(+eventTemplate.id) ? eventTemplate.id : +eventTemplate.id)
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

            PRINTER_TEXT_FIELDS.forEach(textField => (fields[textField.type] = [null, textField.validators]));
            PRINTER_IMAGE_FIELDS.forEach(imageField => (fields[imageField.type] = null));

            this.contentForm = this._fb.group(fields);

            if (this.parentForm) {
                this.contentForm.enable();
                this.parentForm.addControl(GROUP_NAME, this.contentForm);
                this.loadContents();
                this.refreshFormDataHandler();
            } else {
                this.contentForm = null;
            }
        } else {
            this.contentForm = this.parentForm?.get(GROUP_NAME) as UntypedFormGroup;
        }
    }

    private loadContents(): void {
        this.texts$
            .pipe(
                first(elem => !!elem),
                tap(texts => {
                    texts?.length &&
                        PRINTER_TEXT_FIELDS.forEach(textField => {
                            const field = this.contentForm.get(textField.type);
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
                    PRINTER_IMAGE_FIELDS.forEach(imageField => {
                        const field = this.contentForm.get(imageField.type);
                        const image = images?.find(image => image.type === imageField.type);
                        const value = image?.image_url;
                        field.reset(value);
                    });
                })
            ).subscribe();
    }

    private refreshFormDataHandler(): void {
        this.contentForm.valueChanges
            .pipe(
                withLatestFrom(this.texts$, this.images$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([, texts, images]) => {
                images?.length &&
                    PRINTER_IMAGE_FIELDS.forEach(imageField => {
                        const field = this.contentForm.get(imageField.type);
                        const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                        FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                    });
                texts?.length &&
                    PRINTER_TEXT_FIELDS.forEach(textfield => {
                        const field = this.contentForm.get(textfield.type);
                        const originalValue = texts.filter(text => text.type === textfield.type)[0]?.value || '';
                        FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                    });
            });
    }
}
