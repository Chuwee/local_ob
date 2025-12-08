import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    eventTicketImageRestrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    PdfImageType, ProductCommunicationService, productTicketContentTextType, ProductTicketImageContent, ProductTicketImageFields,
    productTicketPdfContentImageType, ProductTicketTextContent, ProductTicketTextFields, productTicketTextRestrictions
} from '@admin-clients/cpanel-products-communication-data-access';
import { ImageUploaderComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, Input, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-product-ticket-content-pdf',
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, ImageUploaderComponent
    ],
    templateUrl: './product-ticket-content-pdf.component.html',
    styleUrls: ['./product-ticket-content-pdf.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductTicketContentPdfComponent implements OnInit, AfterViewInit {
    readonly #fb = inject(FormBuilder);
    readonly #productsService = inject(ProductsService);
    readonly #productCommunicationSrv = inject(ProductCommunicationService);
    readonly #destroyRef = inject(DestroyRef);

    #texts$: Observable<ProductTicketTextContent[]>;
    #images$: Observable<ProductTicketImageContent<PdfImageType>[]>;
    #selectedLanguage: string;
    #productId: number;
    #textRestrictions = productTicketTextRestrictions;
    textType = productTicketContentTextType;
    imageType = productTicketPdfContentImageType;

    #textFields = [
        {
            type: this.textType.name,
            validators: [Validators.maxLength(this.#textRestrictions.nameLength)]
        },
        {
            type: this.textType.delivery,
            validators: [Validators.maxLength(this.#textRestrictions.deliveryLength)]
        }
    ];

    #imageFields = [{ type: productTicketPdfContentImageType }];

    form = input<FormGroup>();
    @Input() language$: Observable<string>;

    imageRestrictions = eventTicketImageRestrictions;
    pdfContentExpanded: boolean;
    pdfContentForm: FormGroup;

    ngOnInit(): void {
        this.initForms();
        this.loadContents();
        this.language$.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(lang => (this.#selectedLanguage = lang));
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.#productCommunicationSrv.ticketsContents.pdf.texts.load(this.#productId);
        this.#productCommunicationSrv.ticketsContents.pdf.images.load(this.#productId);
        this.pdfContentForm.markAsPristine();
    }

    save(
        getTextFields: (
            contentForm: FormGroup, textFields: ProductTicketTextFields[], language: string
        ) => ProductTicketTextContent[],
        getImageFields: (
            contentForm: FormGroup, imageFields: ProductTicketImageFields<PdfImageType>[], language: string
        ) => { [key: string]: ProductTicketImageContent<PdfImageType>[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];
        const textsToSave = getTextFields(this.pdfContentForm, this.#textFields, this.#selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(this.#productCommunicationSrv.ticketsContents.pdf.texts.update(this.#productId, textsToSave));
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.pdfContentForm, this.#imageFields, this.#selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this.#productCommunicationSrv.ticketsContents.pdf.images.post(this.#productId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this.#productCommunicationSrv.ticketsContents.pdf.images.delete(this.#productId, this.#selectedLanguage)
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
    // BACKEND NOT READY YET
    // private openTicketPreview(type: EventTicketTemplateType): void {
    //     this.#eventCommunicationService.downloadTicketPdfPreview$(this.#productId, type, this.#selectedLanguage)
    //         .subscribe(res => window.open(res?.url, '_blank'));
    // }

    private initForms(): void {
        const pdfFields = {};

        this.#textFields.forEach(textField => { pdfFields[textField.type] = [null, textField.validators]; });
        this.#imageFields.forEach(imageField => { pdfFields[imageField.type] = [null]; });

        this.pdfContentForm = this.#fb.group(pdfFields);
        this.form().addControl('pdfContent', this.pdfContentForm);
    }

    private loadContents(): void {
        this.#productsService.product.get$()
            .pipe(
                take(1),
                tap(product => {
                    this.#productId = product.product_id;
                    this.#productCommunicationSrv.ticketsContents.pdf.texts.load(this.#productId);
                    this.#productCommunicationSrv.ticketsContents.pdf.images.load(this.#productId);
                })
            ).subscribe();

        this.#texts$ = combineLatest([
            this.#productCommunicationSrv.ticketsContents.pdf.texts.get$(),
            this.language$
        ]).pipe(
            filter(([pdfTexts, language]) => !!pdfTexts && !!language),
            tap(([pdfTexts, language]) => {
                this.#textFields.forEach(textField => {
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

        this.#images$ = combineLatest([
            this.#productCommunicationSrv.ticketsContents.pdf.images.get$(),
            this.language$
        ]).pipe(
            filter(([images, language]) => !!images && !!language),
            map(([images, language]) =>
                images.filter(img => img.language === language)
            ),
            tap(pdfImages => {
                this.#imageFields.forEach(imageField => {
                    const field = this.pdfContentForm.get(imageField.type);
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
            this.#texts$,
            this.#images$,
            this.form().valueChanges
        ]).pipe(
            filter(data => data.every(item => !!item)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([, , images]) => {
            this.#imageFields.forEach(imageField => {
                const field = this.pdfContentForm.get(imageField.type);
                const originalValue = images.filter(img => img.type === 'BODY')[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
