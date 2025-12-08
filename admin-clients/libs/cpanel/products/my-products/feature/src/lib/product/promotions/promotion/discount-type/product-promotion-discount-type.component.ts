
import { ProductPromotionDiscountType, ProductPromotionType, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { CurrencyInputComponent, EphemeralMessageService, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { greaterThanValidator } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, tap } from 'rxjs';

@Component({
    selector: 'app-product-promotion-discount-type',
    templateUrl: './product-promotion-discount-type.component.html',
    styleUrls: ['./product-promotion-discount-type.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, FlexLayoutModule, TranslatePipe, MatRadioModule,
        CurrencyInputComponent, LocalCurrencyPipe, MatTableModule,
        MatFormFieldModule, MatInputModule, PercentageInputComponent, LocalNumberPipe, MatIconModule
    ]
})
export class ProductPromotionDiscountTypeComponent implements OnInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #productsSrv = inject(ProductsService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly discountTypes = ProductPromotionDiscountType;
    readonly promotionTypes = ProductPromotionType;

    readonly form = this.#fb.group({
        type: [null as ProductPromotionDiscountType, [Validators.required]],
        fixValue: [{ value: null, disabled: true }, [Validators.required, greaterThanValidator(0)]],
        percentualValue: [
            { value: null, disabled: true },
            [Validators.required, greaterThanValidator(0), Validators.max(100)]
        ]
    });

    readonly $loading = toSignal(this.#productsSrv.product.promotion.loading$());
    readonly $promotion = toSignal(this.#productsSrv.product.promotion.get$().pipe(
        tap(promotion => {
            this.#updateForm(promotion.discount);
        }))
    );

    readonly $product = toSignal(this.#productsSrv.product.get$()
        .pipe(
            filter(Boolean),
            map(product => ({
                id: product.product_id,
                currencyCode: product.currency_code
            })),
            takeUntilDestroyed(this.#destroyRef)
        )
    );

    ngOnInit(): void {
        this.#getPromotionTypeChanges();
    }

    cancel(): void {
        this.#loadPromotion();
    }

    save(): void {
        if (this.form.valid) {
            const data = this.form.value;
            const promotion = {
                discount: {
                    type: data.type,
                    value: data.type === ProductPromotionDiscountType.percentage ? data.percentualValue : data.fixValue
                }
            };

            this.#productsSrv.product.promotion.update(this.$product().id, this.$promotion().id, promotion).subscribe({
                next: () => {
                    this.#ephemeralMsgSrv.showSaveSuccess();
                    this.#loadPromotion();
                }
            });
        }
    }

    #loadPromotion(): void {
        this.#productsSrv.product.promotion.load(this.$product().id, this.$promotion().id);
    }

    #updateForm(discount: { type: string; value: number }): void {
        this.form.reset();
        this.form.patchValue({
            type: discount?.type,
            fixValue: discount?.type === ProductPromotionDiscountType.fixed ? discount?.value : null,
            percentualValue: discount?.type === ProductPromotionDiscountType.percentage ? discount?.value : null
        });

        if (discount?.type === ProductPromotionDiscountType.fixed) {
            this.form.get('fixValue').enable();
            this.form.get('percentualValue').disable();
        }

        if (discount?.type === ProductPromotionDiscountType.percentage) {
            this.form.get('percentualValue').enable();
            this.form.get('fixValue').disable();
        }
        this.form.markAsPristine();
    }

    #getPromotionTypeChanges(): void {
        this.form.get('type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((discountType: ProductPromotionDiscountType) => {
                if (discountType === ProductPromotionDiscountType.fixed) {
                    this.form.get('fixValue').enable();
                    this.form.get('percentualValue').disable();
                } else if (discountType === ProductPromotionDiscountType.percentage) {
                    this.form.get('percentualValue').enable();
                    this.form.get('fixValue').disable();
                } else {
                    this.form.get('fixValue').disable();
                    this.form.get('percentualValue').disable();
                }
            });
    }

}
