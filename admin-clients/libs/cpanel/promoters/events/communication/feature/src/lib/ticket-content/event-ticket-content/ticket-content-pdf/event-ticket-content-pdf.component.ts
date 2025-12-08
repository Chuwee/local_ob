import {
    TicketContentFieldsRestrictions as TextRestrictions, EventCommunicationService, EventTicketTemplateType, eventTicketImageRestrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventsService, TicketContentImageType as ImageType, TicketContentImage, TicketContentImageFields, TicketContentImageRequest,
    TicketContentTextType as TextType, TicketContentText, TicketContentTextFields
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketTemplate, TicketTemplatesService, TicketTemplateFormat } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-ticket-content-pdf',
    templateUrl: './event-ticket-content-pdf.component.html',
    styleUrls: ['./event-ticket-content-pdf.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventTicketContentPdfComponent
    implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    private _eventId: number;
    private _images$: Observable<TicketContentImage[]>;
    private _texts$: Observable<TicketContentText[]>;
    private _textFields: TicketContentTextFields[];
    private _imageFields: TicketContentImageFields[];
    private _selectedLanguage: string;

    textType = TextType;
    imageType = ImageType;
    pdfContentExpanded: boolean;
    textRestrictions = TextRestrictions;
    imageRestrictions = eventTicketImageRestrictions;
    pdfContentForm: UntypedFormGroup;
    allowGroups: boolean;
    pdfTemplateSelected$: Observable<TicketTemplate>;

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
        this.prepareFields();
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
        this.pdfContentForm.markAsPristine();
        this._eventCommunicationService.loadEventTicketPdfContentTexts(this.ticketType, this._eventId);
        this._eventCommunicationService.loadEventTicketPdfContentImages(this.ticketType, this._eventId);
    }

    save(
        getTextFields: (contentForm: UntypedFormGroup, textFields: TicketContentTextFields[], language: string) => TicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: TicketContentImageFields[], language: string) =>
            { [key: string]: TicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(this.pdfContentForm, this._textFields, this._selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(this._eventCommunicationService.saveEventTicketPdfContentTexts(this.ticketType, this._eventId, textsToSave));
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.pdfContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(this._eventCommunicationService.saveEventTicketPdfContentImages(this.ticketType, this._eventId, imagesToSave));
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(this._eventCommunicationService.deleteEventTicketPdfContentImages(
                this.ticketType, this._eventId, imagesToDelete
            ));
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1]
                .pipe(
                    tap(() => this.cancel()) // reloads data from backend
                );
        }
        return obsToSave$;
    }

    hasPdfSingleTemplateSelected(): boolean {
        return this.form.get('templatesForm.singlePdfTemplate').value != null;
    }

    hasPdfGroupTemplateSelected(): boolean {
        return this.form.get('templatesForm.groupPdfTemplate').value != null;
    }

    openSingleTicketPreview(): void {
        this.openTicketPreview(this.getSingleTemplateType());
    }

    openGroupTicketPreview(): void {
        this.openTicketPreview(this.getGroupTemplateType());
    }

    private openTicketPreview(type: EventTicketTemplateType): void {
        this._eventCommunicationService.downloadTicketPdfPreview$(this._eventId, type, this._selectedLanguage)
            .subscribe(res => window.open(res?.url, '_blank'));
    }

    private prepareFields(): void {
        this._textFields = [
            {
                type: TextType.title,
                validators: [Validators.maxLength(TextRestrictions.titlePdfLength)]
            },
            {
                type: TextType.subtitle,
                validators: [Validators.maxLength(TextRestrictions.subtitlePdfLength)]
            },
            {
                type: TextType.additionalData,
                validators: [Validators.maxLength(TextRestrictions.additionalDataPdfLength)]
            }
        ];
        this._imageFields = [{ type: ImageType.body }, { type: ImageType.bannerMain }, { type: ImageType.bannerSecondary }];
    }

    private initForms(): void {
        const pdfFields = {};

        this._textFields.forEach(textField => {
            pdfFields[textField.type] = [null];
        });
        this._imageFields.forEach(imageField => {
            pdfFields[imageField.type] = null;
        });

        this.pdfContentForm = this._fb.group(pdfFields);
        this.form.addControl('pdfContent', this.pdfContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._eventService.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._eventId = event.id;
                    this.allowGroups = event.settings.groups?.allowed;
                    this._eventCommunicationService.loadEventTicketPdfContentTexts(this.ticketType, this._eventId);
                    this._eventCommunicationService.loadEventTicketPdfContentImages(this.ticketType, this._eventId);
                })
            ).subscribe();

        this._texts$ = combineLatest([
            this._eventCommunicationService.getEventTicketPdfContentTexts$(),
            this.language$
        ]).pipe(
            filter(([pdfTexts, language]) => !!pdfTexts && !!language),
            tap(([pdfTexts, language]) => {
                this._textFields.forEach(textField => {
                    const field = this.pdfContentForm.get(textField.type);
                    field.reset();
                    for (const text of pdfTexts) {
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
            this._eventCommunicationService.getEventTicketPdfContentImages$(),
            this.language$
        ]).pipe(
            filter(([images, language]) => !!images && !!language),
            map(([images, language]) =>
                images.filter(img => img.language === language)
            ),
            tap(images => {
                this._imageFields.forEach(imageField => {
                    const field = this.pdfContentForm.get(imageField.type);
                    field.reset();
                    for (const image of images) {
                        if (image.type === imageField.type) {
                            field.setValue(image.image_url);
                            return;
                        }
                    }
                });
            })
        );

        this.pdfTemplateSelected$ = combineLatest([
            this.form.get('templatesForm.singlePdfTemplate').valueChanges,
            this._ticketTemplatesService.getTicketTemplates$()
                .pipe(filter(templates => !!templates))
        ]).pipe(
            map(([templateId, templates]) =>
                templates.filter(template =>
                    template.design.format === TicketTemplateFormat.pdf && template.id === templateId)[0]
            )
        );
    }

    private getSingleTemplateType(): EventTicketTemplateType {
        return this.ticketType === TicketType.general
            ? EventTicketTemplateType.single
            : EventTicketTemplateType.singleInvitation;
    }

    private getGroupTemplateType(): EventTicketTemplateType {
        return this.ticketType === TicketType.general
            ? EventTicketTemplateType.group
            : EventTicketTemplateType.groupInvitation;
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._texts$,
            this._images$,
            this.form.valueChanges
        ])
            .pipe(
                filter(
                    ([language, texts, images]) =>
                        !!language && !!texts && !!images
                ),
                takeUntil(this._onDestroy)
            )
            .subscribe(([, , images]) => {
                this._imageFields.forEach(imageField => {
                    const field = this.pdfContentForm.get(imageField.type);
                    const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                    FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
                });
            });
    }
}
