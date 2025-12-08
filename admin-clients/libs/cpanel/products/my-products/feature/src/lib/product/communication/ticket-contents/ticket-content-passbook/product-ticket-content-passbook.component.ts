import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { eventTicketImageRestrictions } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    PassbookImageType, ProductCommunicationService, productTicketContentTextType, ProductTicketImageContent,
    ProductTicketImageFields, productTicketPassbookContentImageType, ProductTicketTextContent, ProductTicketTextFields,
    productTicketTextRestrictions
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
    selector: 'app-product-ticket-content-passbook',
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe, ImageUploaderComponent, FormControlErrorsComponent
    ],
    templateUrl: './product-ticket-content-passbook.component.html',
    styleUrls: ['./product-ticket-content-passbook.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductTicketContentPassbookComponent implements OnInit, AfterViewInit {
    readonly #fb = inject(FormBuilder);
    readonly #productsService = inject(ProductsService);
    readonly #productCommunicationSrv = inject(ProductCommunicationService);
    readonly #destroyRef = inject(DestroyRef);

    #texts$: Observable<ProductTicketTextContent[]>;
    #images$: Observable<ProductTicketImageContent<PassbookImageType>[]>;
    #selectedLanguage: string;
    #productId: number;
    #textRestrictions = productTicketTextRestrictions;
    textType = productTicketContentTextType;
    imageType = productTicketPassbookContentImageType;

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

    #imageFields = [{ type: productTicketPassbookContentImageType }];

    form = input<FormGroup>();
    @Input() language$: Observable<string>;

    imageRestrictions = eventTicketImageRestrictions;
    passbookContentExpanded: boolean;
    passbookContentForm: FormGroup;

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
        this.#productCommunicationSrv.ticketsContents.passbook.texts.load(this.#productId);
        this.#productCommunicationSrv.ticketsContents.passbook.images.load(this.#productId);
        this.passbookContentForm.markAsPristine();
    }

    save(
        getTextFields: (
            contentForm: FormGroup, textFields: ProductTicketTextFields[], language: string
        ) => ProductTicketTextContent[],
        getImageFields: (
            contentForm: FormGroup, imageFields: ProductTicketImageFields<PassbookImageType>[], language: string
        ) => { [key: string]: ProductTicketImageContent<PassbookImageType>[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];
        const textsToSave = getTextFields(this.passbookContentForm, this.#textFields, this.#selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(this.#productCommunicationSrv.ticketsContents.passbook.texts.update(this.#productId, textsToSave));
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.passbookContentForm, this.#imageFields, this.#selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this.#productCommunicationSrv.ticketsContents.passbook.images.post(this.#productId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this.#productCommunicationSrv.ticketsContents.passbook.images.delete(this.#productId, this.#selectedLanguage)
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
    // downloadPassbook(): void {
    // this.#productCommunicationSrv.getDownloadUrlPassbookPreview$(this.#productId)
    //     .pipe(first())
    //     .subscribe(({ download_url: downloadUrl }) =>
    //         window.open(downloadUrl, '_blank')
    //     );
    // }

    private initForms(): void {
        const passbookFields = {};
        this.#textFields.forEach(textField => { passbookFields[textField.type] = [null, textField.validators]; });
        this.#imageFields.forEach(imageField => { passbookFields[imageField.type] = [null]; });
        this.passbookContentForm = this.#fb.group(passbookFields);
        this.form().addControl('passbookContent', this.passbookContentForm);
    }

    private loadContents(): void {
        this.#productsService.product.get$()
            .pipe(
                take(1),
                tap(product => {
                    this.#productId = product.product_id;
                    this.#productCommunicationSrv.ticketsContents.passbook.texts.load(this.#productId);
                    this.#productCommunicationSrv.ticketsContents.passbook.images.load(this.#productId);
                })
            ).subscribe();

        this.#texts$ = combineLatest([
            this.#productCommunicationSrv.ticketsContents.passbook.texts.get$(),
            this.language$
        ]).pipe(
            filter(([passbookTexts, language]) => !!passbookTexts && !!language),
            tap(([passbookTexts, language]) => {
                this.#textFields.forEach(textField => {
                    const field = this.passbookContentForm.get(textField.type);
                    field.reset();
                    for (const text of passbookTexts) {
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
            this.#productCommunicationSrv.ticketsContents.passbook.images.get$(),
            this.language$
        ]).pipe(
            filter(([images, language]) => !!images && !!language),
            map(([images, language]) =>
                images.filter(img => img.language === language)
            ),
            tap(passbookImages => {
                this.#imageFields.forEach(imageField => {
                    const field = this.passbookContentForm.get(imageField.type);
                    field.reset();
                    for (const image of passbookImages) {
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
                const field = this.passbookContentForm.get(imageField.type);
                const originalValue =
                    images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
