import {
    PutSaleRequestTicketContentImage,
    SaleRequestTicketContentImageType,
    SaleRequestTicketContentImage,
    SaleRequestTicketContentImageField,
    SaleRequestTicketContentTemplate,
    SaleRequestTicketContentTextType,
    SaleRequestTicketContentText,
    saleRequestTicketImageRestrictions,
    SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-ticket-content-pdf',
    templateUrl: './sale-request-ticket-content-pdf.component.html',
    styleUrls: ['./sale-request-ticket-content-pdf.component.scss', '../sale-request-ticket-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestTicketContentPdfComponent implements OnInit, AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    private _salesRequestId: number;
    private _images$: Observable<SaleRequestTicketContentImage[]>;
    private _imageFields: SaleRequestTicketContentImageField[];
    private _selectedLanguage: string;
    private _noEditableImages = [SaleRequestTicketContentImageType.body, SaleRequestTicketContentImageType.bannerMain,
    SaleRequestTicketContentImageType.bannerSecondary];

    isPdfContentExpanded: boolean;
    imageRestrictions = saleRequestTicketImageRestrictions;
    saleRequestTicketContentTextType = SaleRequestTicketContentTextType;
    saleRequestTicketContentImageType = SaleRequestTicketContentImageType;
    pdfContentForm: UntypedFormGroup;
    template$: Observable<SaleRequestTicketContentTemplate>;
    texts$: Observable<Record<SaleRequestTicketContentTextType, SaleRequestTicketContentText>>;
    images$: Observable<Record<SaleRequestTicketContentImageType, string>>;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _salesRequestsService: SalesRequestsService
    ) { }

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
        this._salesRequestsService.clearTicketPdfContentTexts();
        this._salesRequestsService.clearTicketPdfContentImages();
        this._salesRequestsService.clearTicketPdfContentTemplate();
    }

    cancel(): void {
        this.pdfContentForm.markAsPristine();
        this._salesRequestsService.loadTicketPdfContentImages(this._salesRequestId);
    }

    save(getImageFields: (contentForm: UntypedFormGroup, imageFields: SaleRequestTicketContentImageField[], language: string) =>
        { [key: string]: PutSaleRequestTicketContentImage[] }
    ): Observable<void>[] {
        const obsToSave$: Observable<void>[] = [];
        const { imagesToSave, imagesToDelete } = getImageFields(this.pdfContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(this._salesRequestsService.saveTicketPdfContentImages(this._salesRequestId, imagesToSave).pipe(
                tap(() => this._salesRequestsService.loadTicketPdfContentImages(this._salesRequestId))
            ));
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(this._salesRequestsService.deleteTicketPdfContentImages(this._salesRequestId, imagesToDelete).pipe(
                tap(() => this._salesRequestsService.loadTicketPdfContentImages(this._salesRequestId))
            ));
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1].pipe(
                tap(() => this.pdfContentForm.markAsPristine())
            );
        }
        return obsToSave$;
    }

    openTicketPreview(): void {
        this._salesRequestsService.downloadTicketPdfPreview$(this._salesRequestId, this._selectedLanguage)
            .subscribe(res => window.open(res?.url, '_blank'));
    }

    private prepareFields(): void {
        this._imageFields = [{
            formField: 'header',
            type: SaleRequestTicketContentImageType.header
        }, {
            formField: 'banner',
            type: SaleRequestTicketContentImageType.banner
        }];
    }

    private initForms(): void {
        const pdfFields: Record<string, unknown> = {};

        this._imageFields.forEach(imageField => {
            pdfFields[imageField.formField] = null;
        });

        this.pdfContentForm = this._fb.group(pdfFields);
        this.form.addControl('pdfContent', this.pdfContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._salesRequestsService.getSaleRequest$()
            .pipe(
                take(1),
                tap(saleRequest => {
                    this._salesRequestId = saleRequest.id;
                    this._salesRequestsService.loadTicketPdfContentTexts(this._salesRequestId);
                    this._salesRequestsService.loadTicketPdfContentImages(this._salesRequestId);
                    this._salesRequestsService.loadTicketPdfContentTemplate(this._salesRequestId);
                })
            ).subscribe();

        this.template$ = this._salesRequestsService.getTicketPdfContentTemplate$()
            .pipe(filter(template => !!template));

        this.texts$ = combineLatest([
            this._salesRequestsService.getTicketPdfContentTexts$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([pdfTexts, language]) => {
                const noEditableTexts = {} as Record<SaleRequestTicketContentTextType, SaleRequestTicketContentText>;
                pdfTexts?.filter(text => text.language === language)
                    .forEach(comText => {
                        noEditableTexts[comText.type] = comText;
                    });
                return noEditableTexts;
            })
        );

        this.images$ = combineLatest([
            this._salesRequestsService.getTicketPdfContentImages$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([pdfImages, language]) => {
                const noEditableImages = {} as Record<SaleRequestTicketContentImageType, string>;
                pdfImages?.filter(image => image.language === language && this._noEditableImages.includes(image.type))
                    .forEach(comText => {
                        noEditableImages[comText.type] = comText.image_url;
                    });
                return noEditableImages;
            })
        );

        this._images$ = combineLatest([
            this._salesRequestsService.getTicketPdfContentImages$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([pdfImages, language]) => pdfImages.filter(img => img.language === language)),
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
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._images$,
            this.form.valueChanges
        ]).pipe(
            filter(data => data.every(item => !!item)),
            takeUntil(this._onDestroy)
        ).subscribe(([, images]) => {
            this._imageFields.forEach(imageField => {
                const field = this.pdfContentForm.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
