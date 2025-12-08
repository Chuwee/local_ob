
import { ProductChannelContentImageType, ProductChannelContentTextType, ProductChannelImageContent, productChannelSliderRestrictions, ProductChannelTextContent, ProductFieldsRestriction, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { ProductsSaleRequestsService } from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { ImageUploaderComponent, LanguageBarComponent } from '@admin-clients/shared/common/ui/components';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatAccordion, MatExpansionModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, map, tap } from 'rxjs';

@Component({
    selector: 'app-product-sale-request-channel-content',
    standalone: true,
    imports: [
        TranslatePipe, AsyncPipe, ImageUploaderComponent, MatProgressSpinner,
        MatAccordion, MatExpansionPanel, MatExpansionPanelTitle, MatExpansionPanelHeader,
        LanguageBarComponent, FormContainerComponent, ReactiveFormsModule, MatExpansionModule, MatFormFieldModule
    ],
    templateUrl: './product-sale-request-channel-content.component.html',
    styleUrls: ['./product-sale-request-channel-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProductSaleRequestChannelContentComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #productsSaleRequestsSrv = inject(ProductsSaleRequestsService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #sanitizer = inject(DomSanitizer);

    readonly fieldsRestrictions = ProductFieldsRestriction;
    readonly sliderRestrictions = productChannelSliderRestrictions;

    descriptionSafeHtml: SafeHtml = '';
    notesSafeHtml: SafeHtml = '';

    #language = new BehaviorSubject<string>(null);

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#productsSrv.product.languages.inProgress$(),
        this.#productsSrv.product.channelContents.texts.loading$(),
        this.#productsSrv.product.channelContents.images.loading$()
    ]);

    readonly form = this.#fb.group({
        name: ['', Validators.maxLength(ProductFieldsRestriction.productNameLength)],
        description: ['', htmlContentMaxLengthValidator(ProductFieldsRestriction.productDescriptionLength)],
        notes: ['', htmlContentMaxLengthValidator(ProductFieldsRestriction.productNotesLength)],
        slider1: null as { data: string; name: string; altText: string },
        slider2: null as { data: string; name: string; altText: string },
        slider3: null as { data: string; name: string; altText: string },
        slider4: null as { data: string; name: string; altText: string },
        slider5: null as { data: string; name: string; altText: string }
    });

    readonly $productText = toSignal(this.#productsSrv.product.channelContents.texts.get$());

    readonly $productImage = toSignal(this.#productsSrv.product.channelContents.images.get$());

    readonly languageList$ = this.#productsSrv.product.languages.get$()
        .pipe(
            filter(Boolean),
            tap(languages => this.#language.next(languages?.find(lang => lang.is_default).code)),
            map(languages => languages.map(lang => lang.code))
        );

    readonly language$ = this.#language.asObservable();

    ngOnInit(): void {
        this.#productsSaleRequestsSrv.productSaleRequest.get$().pipe(
            filter(Boolean)
        ).subscribe(req => {
            const productId = req.product.product_id;
            this.#productsSrv.product.languages.load(productId);
            this.#productsSrv.product.channelContents.texts.load(productId);
            this.#productsSrv.product.channelContents.images.load(productId);
        });

        this.refreshFormDataHandler();
    }

    changeLanguage(newLanguage: string): void {
        this.#language.next(newLanguage);
    }

    private refreshFormDataHandler(): void {
        this.form.disable();

        combineLatest([
            this.language$,
            this.#productsSrv.product.channelContents.texts.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([language, texts]) => {
            this.applyOnTextFields(language, texts, (field, value) => {
                field.setValue(value);

                if (field === this.form.get('description')) {
                    if (value) {
                        this.descriptionSafeHtml = this.#sanitizer.bypassSecurityTrustHtml(value);
                    } else {
                        this.descriptionSafeHtml = null;
                    }
                }
                if (field === this.form.get('notes')) {
                    if (value) {
                        this.notesSafeHtml = this.#sanitizer.bypassSecurityTrustHtml(value);
                    } else {
                        this.notesSafeHtml = null;
                    }
                }
            });
        });

        combineLatest([
            this.language$,
            this.#productsSrv.product.channelContents.images.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([language, images]) => {
            this.applyOnImageFields(language, images, (field, value) => field.setValue(value));
        });
    }

    private applyOnTextFields(
        language: string, texts: ProductChannelTextContent[], doOnField: (field: AbstractControl, value: string) => void
    ): void {
        this.applyOnTextField('name', ProductChannelContentTextType.productName, language, texts, doOnField);
        this.applyOnTextField('description', ProductChannelContentTextType.description, language, texts, doOnField);
        this.applyOnTextField('notes', ProductChannelContentTextType.notes, language, texts, doOnField);
    }

    private applyOnTextField(
        fieldName: string, type: ProductChannelContentTextType, language: string, texts: ProductChannelTextContent[],
        doOnField: (field: AbstractControl, value: string) => void
    ): void {
        const field = this.form.get(fieldName);
        for (const text of texts) {
            if (text.language === language && text.type === type) {
                doOnField(field, text.value);
                return;
            }
        }
        doOnField(field, null);
    }

    private applyOnImageFields(
        language: string, images: ProductChannelImageContent[], doOnField: (field: AbstractControl, value: Partial<ObFile>) => void
    ): void {
        this.applyOnImageField(this.form, 'slider1', ProductChannelContentImageType.landscape, 1, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider2', ProductChannelContentImageType.landscape, 2, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider3', ProductChannelContentImageType.landscape, 3, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider4', ProductChannelContentImageType.landscape, 4, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider5', ProductChannelContentImageType.landscape, 5, language, images, doOnField);
    }

    private applyOnImageField(
        form: UntypedFormGroup, fieldName: string, type: ProductChannelContentImageType, position: number,
        language: string, images: ProductChannelImageContent[],
        doOnField: (field: AbstractControl, value: Partial<ObFile>) => void): void {
        const field = form.get(fieldName);
        for (const image of images) {
            if (image.language === language && image.type === type && (!position || position === image.position)) {
                doOnField(field, { data: image.image_url, altText: image.alt_text });
                field.disable();
                return;
            }
        }
        doOnField(field, null);
    }

}
