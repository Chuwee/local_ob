import { eventTicketImageRestrictions } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketTicketContentImage,
    SeasonTicketTicketContentText,
    SeasonTicketTicketContentTextFields,
    SeasonTicketTicketContentImageFields,
    SeasonTicketTicketContentFieldsRestrictions,
    seasonTicketTicketImageRestrictions,
    SeasonTicketCommunicationService,
    SeasonTicketTicketContentImageRequest,
    SeasonTicketTemplateType,
    SeasonTicketTicketContentTextType,
    SeasonTicketTicketContentImageType
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import {
    TicketTemplate,
    TicketTemplatesService,
    TicketTemplateFormat
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-ticket-content-pdf',
    templateUrl: './season-ticket-ticket-content-pdf.component.html',
    styleUrls: [
        './season-ticket-ticket-content-pdf.component.scss',
        '../season-ticket-ticket-content.component.scss'
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketTicketContentPdfComponent
    implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    private _seasonTicketId: number;
    private _images$: Observable<SeasonTicketTicketContentImage[]>;
    private _texts$: Observable<SeasonTicketTicketContentText[]>;
    private _textFields: SeasonTicketTicketContentTextFields[];
    private _imageFields: SeasonTicketTicketContentImageFields[];
    private _selectedLanguage: string;

    pdfContentExpanded: boolean;
    textRestrictions = SeasonTicketTicketContentFieldsRestrictions;
    imageRestrictions = seasonTicketTicketImageRestrictions;
    pdfContentForm: UntypedFormGroup;
    allowGroups: boolean;
    pdfTemplateSelected$: Observable<TicketTemplate>;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() ticketType = TicketType.general;

    constructor(
        private _fb: UntypedFormBuilder,
        private _seasonTicketService: SeasonTicketsService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _seasonTicketCommunicationService: SeasonTicketCommunicationService
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
        this._seasonTicketCommunicationService.loadSeasonTicketTicketPdfContentTexts(
            this.ticketType,
            this._seasonTicketId
        );
        this._seasonTicketCommunicationService.loadSeasonTicketTicketPdfContentImages(
            this.ticketType,
            this._seasonTicketId
        );
    }

    save(
        getTextFields: (
            contentForm: UntypedFormGroup,
            textFields: SeasonTicketTicketContentTextFields[],
            language: string
        ) => SeasonTicketTicketContentText[],
        getImageFields: (
            contentForm: UntypedFormGroup,
            imageFields: SeasonTicketTicketContentImageFields[],
            language: string
        ) => { [key: string]: SeasonTicketTicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(
            this.pdfContentForm,
            this._textFields,
            this._selectedLanguage
        );
        if (textsToSave.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService.saveSeasonTicketTicketPdfContentTexts(
                    this.ticketType,
                    this._seasonTicketId,
                    textsToSave
                )
            );
        }

        const { imagesToSave, imagesToDelete } = getImageFields(
            this.pdfContentForm,
            this._imageFields,
            this._selectedLanguage
        );
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService.saveSeasonTicketTicketPdfContentImages(
                    this.ticketType,
                    this._seasonTicketId,
                    imagesToSave
                )
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService.deleteSeasonTicketTicketPdfContentImages(
                    this.ticketType,
                    this._seasonTicketId,
                    imagesToDelete
                )
            );
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[
                obsToSave$.length - 1
            ].pipe(
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

    private openTicketPreview(type: SeasonTicketTemplateType): void {
        this._seasonTicketCommunicationService
            .downloadTicketPdfPreview$(
                this._seasonTicketId,
                type,
                this._selectedLanguage
            )
            .subscribe(res => window.open(res?.url, '_blank'));
    }

    private prepareFields(): void {
        this._textFields = [
            {
                formField: 'title',
                type: SeasonTicketTicketContentTextType.title,
                maxLength:
                    SeasonTicketTicketContentFieldsRestrictions.titlePdfLength
            },
            {
                formField: 'subtitle',
                type: SeasonTicketTicketContentTextType.subtitle,
                maxLength:
                    SeasonTicketTicketContentFieldsRestrictions.subtitlePdfLength
            },
            {
                formField: 'additionalData',
                type: SeasonTicketTicketContentTextType.additionalData,
                maxLength:
                    SeasonTicketTicketContentFieldsRestrictions.additionalDataPdfLength
            }
        ];
        this._imageFields = [
            {
                formField: 'body',
                type: SeasonTicketTicketContentImageType.body,
                maxSize: eventTicketImageRestrictions['pdfBody'].size
            },
            {
                formField: 'bannerMain',
                type: SeasonTicketTicketContentImageType.bannerMain,
                maxSize: eventTicketImageRestrictions['pdfBannerMain'].size
            },
            {
                formField: 'bannerSecondary',
                type: SeasonTicketTicketContentImageType.bannerSecondary,
                maxSize: eventTicketImageRestrictions['pdfBannerSecondary'].size
            }
        ];
    }

    private initForms(): void {
        const pdfFields = {};

        this._textFields.forEach(textField => {
            pdfFields[textField.formField] = [
                null,
                [Validators.maxLength(textField.maxLength)]
            ];
        });
        this._imageFields.forEach(imageField => {
            pdfFields[imageField.formField] = null;
        });

        this.pdfContentForm = this._fb.group(pdfFields);
        this.form.addControl('pdfContent', this.pdfContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._seasonTicketService.seasonTicket
            .get$()
            .pipe(
                take(1),
                tap(seasonTicket => {
                    this._seasonTicketId = seasonTicket.id;
                    // this.allowGroups = seasonTicket.settings.groups?.allowed;
                    this._seasonTicketCommunicationService.loadSeasonTicketTicketPdfContentTexts(
                        this.ticketType,
                        this._seasonTicketId
                    );
                    this._seasonTicketCommunicationService.loadSeasonTicketTicketPdfContentImages(
                        this.ticketType,
                        this._seasonTicketId
                    );
                })
            )
            .subscribe();

        this._texts$ = combineLatest([
            this._seasonTicketCommunicationService.getSeasonTicketTicketPdfContentTexts$(),
            this.language$
        ]).pipe(
            filter(([pdfTexts, language]) => !!pdfTexts && !!language),
            tap(([pdfTexts, language]) => {
                this._textFields.forEach(textField => {
                    const field = this.pdfContentForm.get(textField.formField);
                    field.reset();
                    for (const text of pdfTexts) {
                        if (
                            text.language === language &&
                            text.type === textField.type
                        ) {
                            field.setValue(text.value);
                            return;
                        }
                    }
                });
            }),
            map(([texts, _]) => texts)
        );

        this._images$ = combineLatest([
            this._seasonTicketCommunicationService.getSeasonTicketTicketPdfContentImages$(),
            this.language$
        ]).pipe(
            filter(([pdfImages, language]) => !!pdfImages && !!language),
            map(([pdfImages, language]) =>
                pdfImages.filter(img => img.language === language)
            ),
            tap(pdfImages => {
                this._imageFields.forEach(imageField => {
                    const field = this.pdfContentForm.get(imageField.formField);
                    field.reset();
                    for (const image of pdfImages) {
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
            this._ticketTemplatesService
                .getTicketTemplates$()
                .pipe(filter(templates => !!templates))
        ]).pipe(
            map(
                ([templateId, templates]) =>
                    templates.filter(
                        template =>
                            template.design.format ===
                            TicketTemplateFormat.pdf &&
                            template.id === templateId
                    )[0]
            )
        );
    }

    private getSingleTemplateType(): SeasonTicketTemplateType {
        return this.ticketType === TicketType.general
            ? SeasonTicketTemplateType.single
            : SeasonTicketTemplateType.singleInvitation;
    }

    private getGroupTemplateType(): SeasonTicketTemplateType {
        return this.ticketType === TicketType.general
            ? SeasonTicketTemplateType.group
            : SeasonTicketTemplateType.groupInvitation;
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
                    const field = this.pdfContentForm.get(imageField.formField);
                    const originalValue =
                        images.filter(img => img.type === imageField.type)[0]
                            ?.image_url || null;
                    FormControlHandler.checkAndRefreshDirtyState(
                        field,
                        originalValue
                    );
                });
            });
    }
}
