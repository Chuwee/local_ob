import {
    PutSaleRequestTicketContentImage, SaleRequestTicketContentImageType,
    SaleRequestTicketContentImage,
    SaleRequestTicketContentImageField, SaleRequestTicketContentTemplate, SaleRequestTicketContentTextType,
    SaleRequestTicketContentText, saleRequestTicketImageRestrictions, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-ticket-content-printer',
    templateUrl: './sale-request-ticket-content-printer.component.html',
    styleUrls: ['./sale-request-ticket-content-printer.component.scss', '../sale-request-ticket-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestTicketContentPrinterComponent implements OnInit, AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    private _salesRequestId: number;
    private _images$: Observable<SaleRequestTicketContentImage[]>;
    private _imageFields: SaleRequestTicketContentImageField[];
    private _selectedLanguage: string;
    private _noEditableImages = [SaleRequestTicketContentImageType.body];

    isPrinterContentExpanded: boolean;
    imageRestrictions = saleRequestTicketImageRestrictions;
    saleRequestTicketContentTextType = SaleRequestTicketContentTextType;
    saleRequestTicketContentImageType = SaleRequestTicketContentImageType;
    printerContentForm: UntypedFormGroup;
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
        this._salesRequestsService.clearTicketPrinterContentTexts();
        this._salesRequestsService.clearTicketPrinterContentImages();
        this._salesRequestsService.clearTicketPrinterContentTemplate();
    }

    cancel(): void {
        this.printerContentForm.markAsPristine();
        this._salesRequestsService.loadTicketPrinterContentImages(this._salesRequestId);
    }

    save(getImageFields: (contentForm: UntypedFormGroup, imageFields: SaleRequestTicketContentImageField[], language: string) =>
        { [key: string]: PutSaleRequestTicketContentImage[] }
    ): Observable<void>[] {
        const obsToSave$: Observable<void>[] = [];
        const { imagesToSave, imagesToDelete } = getImageFields(this.printerContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(this._salesRequestsService.saveTicketPrinterContentImages(this._salesRequestId, imagesToSave).pipe(
                tap(() => this._salesRequestsService.loadTicketPrinterContentImages(this._salesRequestId))
            ));
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(this._salesRequestsService.deleteTicketPrinterContentImages(this._salesRequestId, imagesToDelete).pipe(
                tap(() => this._salesRequestsService.loadTicketPrinterContentImages(this._salesRequestId))
            ));
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1].pipe(
                tap(() => this.printerContentForm.markAsPristine())
            );
        }
        return obsToSave$;
    }

    private prepareFields(): void {
        this._imageFields = [{
            formField: 'printerBanner',
            type: SaleRequestTicketContentImageType.bannerMain
        }];
    }

    private initForms(): void {
        const printerFields: Record<string, unknown> = {};

        this._imageFields.forEach(imageField => {
            printerFields[imageField.formField] = null;
        });

        this.printerContentForm = this._fb.group(printerFields);
        this.form.addControl('printerContent', this.printerContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._salesRequestsService.getSaleRequest$()
            .pipe(
                take(1),
                tap(saleRequest => {
                    this._salesRequestId = saleRequest.id;
                    this._salesRequestsService.loadTicketPrinterContentTexts(this._salesRequestId);
                    this._salesRequestsService.loadTicketPrinterContentImages(this._salesRequestId);
                    this._salesRequestsService.loadTicketPrinterContentTemplate(this._salesRequestId);
                })
            ).subscribe();

        this.template$ = this._salesRequestsService.getTicketPrinterContentTemplate$()
            .pipe(filter(template => !!template));

        this.texts$ = combineLatest([
            this._salesRequestsService.getTicketPrinterContentTexts$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([printerTexts, language]) => {
                const noEditableTexts = {} as Record<SaleRequestTicketContentTextType, SaleRequestTicketContentText>;
                printerTexts?.filter(text => text.language === language)
                    .forEach(comText => {
                        noEditableTexts[comText.type] = comText;
                    });
                return noEditableTexts;
            })
        );

        this.images$ = combineLatest([
            this._salesRequestsService.getTicketPrinterContentImages$(),
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
            this._salesRequestsService.getTicketPrinterContentImages$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
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
                const field = this.printerContentForm.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
