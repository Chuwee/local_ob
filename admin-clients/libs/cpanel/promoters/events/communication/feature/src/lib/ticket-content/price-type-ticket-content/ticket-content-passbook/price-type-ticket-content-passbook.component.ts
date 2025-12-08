import {
    TicketContentFieldsRestrictions as TextRestrictions, eventTicketImageRestrictions, EventCommunicationService, TicketContentFormat,
    EventTicketTemplateType, eventTicketContetPassbookFieldsRelations
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventsService, TicketContentImageType as ImageType, TicketContentImage, TicketContentTextType as TextType, TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketTemplate } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketPassbook, TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import {
    combineLatest, distinctUntilChanged, filter, first, map, Observable, shareReplay, startWith, Subject, switchMapTo, take,
    takeUntil, tap, withLatestFrom
} from 'rxjs';
import { PASSBOOK_IMAGE_FIELDS, PASSBOOK_TEXT_FIELDS } from '../models/ticket-content-passbook';

const GROUP_NAME = 'passbook';

@Component({
    selector: 'app-price-type-ticket-content-passbook',
    templateUrl: './price-type-ticket-content-passbook.component.html',
    styleUrls: ['./price-type-ticket-content-passbook.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PriceTypeTicketContentPassbookComponent
    implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    textType = TextType;
    imageType = ImageType;
    expanded: boolean;
    textRestrictions = TextRestrictions;
    imageRestrictions = eventTicketImageRestrictions;
    parentForm: UntypedFormGroup;
    contentForm: UntypedFormGroup;
    templateSelected$: Observable<TicketTemplate>;
    placeholders$: Observable<{
        texts?: Record<string, string>;
        images?: Record<string, string>;
    }>;

    ticketPassbook$: Observable<TicketPassbook>;
    pdfTemplateSelected$: Observable<TicketTemplate>;

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
        private _ticketsPassbookService: TicketsPassbookService,
        private _eventCommunicationService: EventCommunicationService
    ) { }

    ngOnInit(): void {
        this._eventService.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._eventCommunicationService.loadEventTicketPassbookContentTexts(this.ticketType, event.id);
                    this._eventCommunicationService.loadEventTicketPassbookContentImages(this.ticketType, event.id);
                })
            ).subscribe();
        this.placeholders$ = combineLatest([
            this.language$,
            this._eventCommunicationService.getEventTicketPassbookContentTexts$(),
            this._eventCommunicationService.getEventTicketPassbookContentImages$()
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

        this.ticketPassbook$ = this._eventCommunicationService.getEventTicketTemplates$()
            .pipe(
                map(eventTemplates => eventTemplates?.find(template =>
                    template.format === TicketContentFormat.passbook &&
                    template.type === EventTicketTemplateType.single
                )
                ),
                filter(eventTemplate => !!eventTemplate),
                distinctUntilChanged((oldEventTemplate, newEventTemplate) => oldEventTemplate.id === newEventTemplate.id),
                withLatestFrom(this._eventService.event.get$()),
                tap(([eventTemplate, event]) =>
                    this._ticketsPassbookService.loadTicketPassbook(`${eventTemplate.id}`, `${event.entity.id}`)
                ),
                switchMapTo(this._ticketsPassbookService.getTicketPassbook$()),
                filter(ticketPassbook => !!ticketPassbook),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    isPassbookFieldInactive$(field?: string): Observable<boolean> {
        return this.ticketPassbook$.pipe(
            filter(ticketPasbook => !!ticketPasbook),
            map(ticketPassbook => {
                const ticketPassbookValues = Object.values(ticketPassbook);
                if (field) {
                    // Check if X field exist in passbook
                    const passbookFields = Object.entries(eventTicketContetPassbookFieldsRelations)
                        .filter(([, value]) => value === field)
                        .map(([key]) => key);
                    return !ticketPassbookValues.some(properties =>
                        passbookFields.includes(properties?.key) &&
                        field === eventTicketContetPassbookFieldsRelations[properties?.key]
                    );
                } else {
                    // Check if any field exist in passbook
                    const passbookFieldKeys = Object.keys(eventTicketContetPassbookFieldsRelations);
                    const filteredField = ticketPassbookValues
                        .filter(properties => properties?.key)
                        .map(({ key }) => key);
                    return !passbookFieldKeys.some(key => filteredField.includes(key));
                }
            })
        );
    }

    private initForms(): void {
        if (!this.parentForm?.get(GROUP_NAME)) {
            const fields = {};

            PASSBOOK_TEXT_FIELDS.forEach(textField => (fields[textField.type] = [null, textField.validators]));
            PASSBOOK_IMAGE_FIELDS.forEach(imageField => (fields[imageField.type] = null));

            this.contentForm = this._fb.group(fields);

            if (this.parentForm) {
                this.contentForm.enable();
                this.parentForm.addControl(GROUP_NAME, this.contentForm);
                this.loadContents();
                this.refreshFormDataHandler();
            } else {
                this.contentForm.disable();
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
                        PASSBOOK_TEXT_FIELDS.forEach(textField => {
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
                    PASSBOOK_IMAGE_FIELDS.forEach(imageField => {
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
                    PASSBOOK_IMAGE_FIELDS.forEach(imageField => {
                        const field = this.contentForm.get(imageField.type);
                        const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                        FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                    });
                texts?.length &&
                    PASSBOOK_TEXT_FIELDS.forEach(textfield => {
                        const field = this.contentForm.get(textfield.type);
                        const originalValue = texts.filter(text => text.type === textfield.type)[0]?.value || '';
                        FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                    });
            });
    }
}
