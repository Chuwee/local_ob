import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketTicketContentImage, SeasonTicketTicketContentText, SeasonTicketTicketContentTextFields,
    SeasonTicketTicketContentImageFields, SeasonTicketTicketContentFieldsRestrictions, seasonTicketTicketImageRestrictions,
    SeasonTicketCommunicationService, SeasonTicketTicketContentImageRequest, SeasonTicketTicketContentTextType,
    SeasonTicketTicketContentImageType
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import { TicketTemplate, TicketTemplatesService, TicketTemplateFormat } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-ticket-content-printer',
    templateUrl: './season-ticket-ticket-content-printer.component.html',
    styleUrls: ['./season-ticket-ticket-content-printer.component.scss', '../season-ticket-ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketTicketContentPrinterComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    private _seasonTicketId: number;
    private _images$: Observable<SeasonTicketTicketContentImage[]>;
    private _texts$: Observable<SeasonTicketTicketContentText[]>;
    private _textFields: SeasonTicketTicketContentTextFields[];
    private _imageFields: SeasonTicketTicketContentImageFields[];
    private _selectedLanguage: string;

    printerContentExpanded: boolean;
    textRestrictions = SeasonTicketTicketContentFieldsRestrictions;
    imageRestrictions = seasonTicketTicketImageRestrictions;
    printerContentForm: UntypedFormGroup;
    printerTemplateSelected$: Observable<TicketTemplate>;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() ticketType = TicketType.general;

    constructor(
        private _fb: UntypedFormBuilder,
        private _seasonTicketService: SeasonTicketsService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _seasonTicketCommunicationService: SeasonTicketCommunicationService) {
    }

    ngOnInit(): void {
        this.prepareFields();
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
    }

    cancel(): void {
        this.printerContentForm.markAsPristine();
        this._seasonTicketCommunicationService.loadSeasonTicketTicketPrinterContentTexts(this.ticketType, this._seasonTicketId);
        this._seasonTicketCommunicationService.loadSeasonTicketTicketPrinterContentImages(this.ticketType, this._seasonTicketId);
    }

    save(getTextFields: (contentForm: UntypedFormGroup, textFields: SeasonTicketTicketContentTextFields[], language: string) =>
        SeasonTicketTicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: SeasonTicketTicketContentImageFields[], language: string) =>
            { [key: string]: SeasonTicketTicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(this.printerContentForm, this._textFields, this._selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService
                    .saveSeasonTicketTicketPrinterContentTexts(this.ticketType, this._seasonTicketId, textsToSave)
            );
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.printerContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService
                    .saveSeasonTicketTicketPrinterContentImages(this.ticketType, this._seasonTicketId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService
                    .deleteSeasonTicketTicketPrinterContentImages(this.ticketType, this._seasonTicketId, imagesToDelete)
            );
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1].pipe(
                tap(() => this.cancel()) // reloads data from backend
            );
        }
        return obsToSave$;
    }

    private prepareFields(): void {
        this._textFields = [{
            formField: 'title',
            type: SeasonTicketTicketContentTextType.title,
            maxLength: SeasonTicketTicketContentFieldsRestrictions.titlePrinterLength
        }, {
            formField: 'subtitle',
            type: SeasonTicketTicketContentTextType.subtitle,
            maxLength: SeasonTicketTicketContentFieldsRestrictions.subtitlePrinterLength
        }, {
            formField: 'additionalData',
            type: SeasonTicketTicketContentTextType.additionalData,
            maxLength: SeasonTicketTicketContentFieldsRestrictions.additionalDataPrinterLength
        }];
        this._imageFields = [{
            formField: 'body',
            type: SeasonTicketTicketContentImageType.body,
            maxSize: seasonTicketTicketImageRestrictions.printerBody.size
        }, {
            formField: 'bannerMain',
            type: SeasonTicketTicketContentImageType.bannerMain,
            maxSize: seasonTicketTicketImageRestrictions.printerBannerMain.size
        }];
    }

    private initForms(): void {
        const printerFields = {};

        this._textFields.forEach(textField => {
            printerFields[textField.formField] = [null, [Validators.maxLength(textField.maxLength)]];
        });
        this._imageFields.forEach(imageField => {
            printerFields[imageField.formField] = null;
        });

        this.printerContentForm = this._fb.group(printerFields);
        this.form.addControl('printerContent', this.printerContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._seasonTicketService.seasonTicket.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._seasonTicketId = event.id;
                    this._seasonTicketCommunicationService.loadSeasonTicketTicketPrinterContentTexts(this.ticketType, this._seasonTicketId);
                    this._seasonTicketCommunicationService
                        .loadSeasonTicketTicketPrinterContentImages(this.ticketType, this._seasonTicketId);
                })
            ).subscribe();

        this._texts$ = combineLatest([
            this._seasonTicketCommunicationService.getSeasonTicketTicketPrinterContentTexts$(),
            this.language$
        ]).pipe(
            filter(([printerTexts, language]) => !!printerTexts && !!language),
            tap(([printerTexts, language]) => {
                this._textFields.forEach(textField => {
                    const field = this.printerContentForm.get(textField.formField);
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
            this._seasonTicketCommunicationService.getSeasonTicketTicketPrinterContentImages$(),
            this.language$
        ]).pipe(
            filter(([printerImages, language]) => !!printerImages && !!language),
            map(([printerImages, language]) => printerImages.filter(img => img.language === language)),
            tap(printerImages => {
                this._imageFields.forEach(imageField => {
                    const field = this.printerContentForm.get(imageField.formField);
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
            this._ticketTemplatesService.getTicketTemplates$().pipe(filter(templates => !!templates))
        ]).pipe(
            map(([templateId, templates]) => templates
                .filter(template => template.design.format === TicketTemplateFormat.printer && template.id === templateId)[0]));
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._texts$,
            this._images$,
            this.form.valueChanges
        ]).pipe(
            filter(([language, texts, images]) => !!language && !!texts && !!images),
            takeUntil(this._onDestroy)
        ).subscribe(([, , images]) => {
            this._imageFields.forEach(imageField => {
                const field = this.printerContentForm.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
