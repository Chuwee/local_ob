import {
    SalesRequestsService,
    PutSaleRequestPurchaseContentImage,
    SaleRequestPurchaseContentImageType,
    SaleRequestPurchaseContentImage,
    SaleRequestPurchaseContentImageField,
    SaleRequestPurchaseContentTextType,
    SaleRequestPurchaseContentText,
    SaleRequestPurchaseContentTextField,
    saleRequestPurchaseImageRestrictions
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { FormControlHandler, urlValidator } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-purchase-contents',
    templateUrl: './sale-request-purchase-contents.component.html',
    styleUrls: ['./sale-request-purchase-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestPurchaseContentsComponent implements OnInit, AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    private _saleRequestId: number;
    private _textFields: SaleRequestPurchaseContentTextField[];
    private _imageFields: SaleRequestPurchaseContentImageField[];
    private _images$: Observable<SaleRequestPurchaseContentImage[]>;
    private _texts$: Observable<SaleRequestPurchaseContentText[]>;
    private _selectedLanguage: string;

    imageRestrictions = saleRequestPurchaseImageRestrictions;
    purchaseContentsForm: UntypedFormGroup;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _saleRequestsService: SalesRequestsService,
        private _ref: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();
        this.language$.pipe(takeUntil(this._onDestroy)).subscribe(lang => this._selectedLanguage = lang);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._saleRequestsService.clearSaleRequestPurchaseContentImages();
        this._saleRequestsService.clearSaleRequestPurchaseContentTexts();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.purchaseContentsForm.markAsPristine();
        this._saleRequestsService.loadSaleRequestPurchaseContentTexts(this._saleRequestId);
        this._saleRequestsService.loadSaleRequestPurchaseContentImages(this._saleRequestId);
    }

    getImageFields(
        contentForm: UntypedFormGroup, imageFields: SaleRequestPurchaseContentImageField[], language: string
    ): { [key: string]: PutSaleRequestPurchaseContentImage[] } {
        const imagesToSave: PutSaleRequestPurchaseContentImage[] = [];
        const imagesToDelete: PutSaleRequestPurchaseContentImage[] = [];
        imageFields.forEach(imageField => {
            const field = contentForm.get(imageField.formField);
            if (field.dirty) {
                const imageValue = field.value;
                if (imageValue?.data) {
                    imagesToSave.push({ type: imageField.type, image: imageValue?.data, language, alt_text: imageValue?.altText });
                } else {
                    imagesToDelete.push({ type: imageField.type, image: null, language });
                }
            }
        });
        return { imagesToSave, imagesToDelete };
    }

    getTextFields(
        contentForm: UntypedFormGroup,
        textFields: SaleRequestPurchaseContentTextField[],
        language: string
    ): SaleRequestPurchaseContentText[] {
        const textsToSave: SaleRequestPurchaseContentText[] = [];
        textFields.forEach(textField => {
            const field = contentForm.get(textField.formField);
            if (field.dirty) {
                textsToSave.push({ type: textField.type, redirect_url: field.value, language });
            }
        });
        return textsToSave;
    }

    save(): Observable<void>[] {
        const obs$: Observable<void>[] = [];

        const textsToSave = this.getTextFields(this.purchaseContentsForm, this._textFields, this._selectedLanguage);
        if (textsToSave.length > 0) {
            obs$.push(this._saleRequestsService.saveSaleRequestPurchaseContentTexts(this._saleRequestId, textsToSave).pipe(
                tap(() => this._saleRequestsService.loadSaleRequestPurchaseContentTexts(this._saleRequestId))
            ));
        }

        const { imagesToSave, imagesToDelete } = this.getImageFields(
            this.purchaseContentsForm,
            this._imageFields,
            this._selectedLanguage
        );
        if (imagesToSave.length > 0) {
            obs$.push(this._saleRequestsService.saveSaleRequestPurchaseContentImages(this._saleRequestId, imagesToSave).pipe(
                tap(() => this._saleRequestsService.loadSaleRequestPurchaseContentImages(this._saleRequestId))
            ));
        }
        if (imagesToDelete.length > 0) {
            obs$.push(this._saleRequestsService.deleteSaleRequestPurchaseContentImages(this._saleRequestId, imagesToDelete).pipe(
                tap(() => this._saleRequestsService.loadSaleRequestPurchaseContentImages(this._saleRequestId))
            ));
        }

        if (obs$.length) {
            obs$[obs$.length - 1] = obs$[obs$.length - 1].pipe(
                tap(() => this.purchaseContentsForm.markAsPristine())
            );
        }

        return obs$;
    }

    private prepareFields(): void {
        this._imageFields = [{
            formField: 'promoterBanner',
            type: SaleRequestPurchaseContentImageType.promoterBanner
        }, {
            formField: 'headerBanner',
            type: SaleRequestPurchaseContentImageType.headerBanner
        }, {
            formField: 'banner',
            type: SaleRequestPurchaseContentImageType.banner
        }];
        this._textFields = [{
            formField: 'headerBannerUrl',
            type: SaleRequestPurchaseContentTextType.headerBanner
        }, {
            formField: 'bannerUrl',
            type: SaleRequestPurchaseContentTextType.banner
        }];
    }

    private initForms(): void {
        const purchaseFields: Record<string, unknown> = {};
        this._imageFields.forEach(imageField => {
            purchaseFields[imageField.formField] = null;
        });
        this._textFields.forEach(textField => {
            purchaseFields[textField.formField] = [null, urlValidator()];
        });
        this.purchaseContentsForm = this._fb.group(purchaseFields);
        this.form.addControl('purchaseContentsForm', this.purchaseContentsForm);
    }

    private loadContents(): void {
        this._saleRequestsService.getSaleRequest$()
            .pipe(
                filter(value => !!value),
                takeUntil(this._onDestroy)
            ).subscribe(saleRequest => {
                this._saleRequestId = saleRequest.id;
                this._saleRequestsService.loadSaleRequestPurchaseContentImages(this._saleRequestId);
                this._saleRequestsService.loadSaleRequestPurchaseContentTexts(this._saleRequestId);
            }
            );

        this._texts$ = combineLatest([
            this._saleRequestsService.getSaleRequestPurchaseContentTexts$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([purchaseTexts, language]) => purchaseTexts.filter(text => text.language === language)),
            tap(purchaseTexts => {
                this._textFields.forEach(textField => {
                    const field = this.purchaseContentsForm.get(textField.formField);
                    field.reset();
                    for (const text of purchaseTexts) {
                        if (text.type === textField.type) {
                            field.setValue(text.redirect_url);
                            return;
                        }
                    }
                });
                this._ref.detectChanges();
            })
        );

        this._images$ = combineLatest([
            this._saleRequestsService.getSaleRequestPurchaseContentImages$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([purchaseImgs, language]) => purchaseImgs.filter(img => img.language === language)),
            tap(purchaseImgs => {
                this._imageFields.forEach(imageField => {
                    const field = this.purchaseContentsForm.get(imageField.formField);
                    field.reset();
                    for (const image of purchaseImgs) {
                        if (image.type === imageField.type) {
                            field.setValue({
                                data: image.image_url,
                                altText: image.alt_text
                            });
                            return;
                        }
                    }
                });
                this.form.markAsPristine();
            })
        );

    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._images$,
            this._texts$,
            this.form.valueChanges
        ]).pipe(
            filter(data => data.every(item => !!item)),
            takeUntil(this._onDestroy)
        ).subscribe(([_, images, texts]) => {
            this._imageFields.forEach(imageField => {
                const field = this.purchaseContentsForm.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
            this._textFields.forEach(textField => {
                const field = this.purchaseContentsForm.get(textField.formField);
                const originalValue = texts.filter(text => text.type === textField.type)[0]?.redirect_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
