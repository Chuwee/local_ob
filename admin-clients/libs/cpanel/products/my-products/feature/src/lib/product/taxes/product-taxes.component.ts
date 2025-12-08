import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    ProductStatus, ProductSurcharge, ProductSurchargeType, ProductsService, productSurchargeType, ProductTaxesMode
} from '@admin-clients/cpanel/products/my-products/data-access';
import { cleanRangesBeforeSave, EphemeralMessageService, RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject, viewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, forkJoin, map, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-product-taxes',
    imports: [
        AsyncPipe, FormContainerComponent, MatTooltipModule, RangeTableComponent,
        MatExpansionModule, TranslatePipe, MatProgressSpinnerModule,
        MatFormFieldModule, MatSelectModule, ReactiveFormsModule, MatRadioGroup, MatRadioButton
    ],
    templateUrl: './product-taxes.component.html',
    styleUrls: ['./product-taxes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductTaxesComponent implements OnInit, OnDestroy {
    readonly #destroy = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #productsSrv = inject(ProductsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    private readonly _matExpansionPanelQueryList = viewChildren(MatExpansionPanel);

    // TODO: Remove isTestTaxes when  taxes end
    readonly isTestTaxes = false;

    readonly $inProgress = toSignal(booleanOrMerge([
        this.#entitiesSrv.isEntityTaxesLoading(),
        this.#entitiesSrv.isEntityLoading$(),
        this.#productsSrv.product.inProgress$()
    ]));

    readonly $entityTaxes = toSignal(this.#entitiesSrv.getEntityTaxes$().pipe(filter(Boolean)));

    readonly $product = toSignal(this.#productsSrv.product.get$().pipe(filter(Boolean), tap(product => {
        this.#entitiesSrv.loadEntityTaxes(product.entity.id);
        this.#productsSrv.product.surcharges.load(product.product_id);
    })));

    readonly $currency = toSignal(this.#productsSrv.product.get$()
        .pipe(map(product => product.currency_code)));

    readonly productStatus = ProductStatus;
    readonly surchargesTypes = productSurchargeType;
    readonly surcharges$ = this.#productsSrv.product.surcharges.get$().pipe(filter(Boolean));

    readonly form = this.#fb.group({
        taxes: [{ value: this.$product().tax?.id, disabled: true }, Validators.required],
        surcharges_taxes: [{ value: this.$product().surcharge_tax?.id, disabled: true }, Validators.required],
        surcharges: this.#fb.nonNullable.group({}),
        tax_mode: { value: this.$product()?.settings?.tax_mode ?? null as ProductTaxesMode, disabled: true }
    });

    surcharges = new Map<ProductSurchargeType, RangeElement[]>();
    readonly productTaxesMode = ProductTaxesMode;

    ngOnInit(): void {
        this.surcharges$.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroy)
        ).subscribe(productSurcharges => {
            this.initMap();
            productSurcharges.forEach(surcharge => {
                this.surcharges.set(surcharge.type, surcharge.ranges);
                this.form.controls.surcharges.addControl(this.surchargesTypes.generic, this.#fb.group({}));
                this.form.controls.surcharges.addControl(this.surchargesTypes.promotion, this.#fb.group({}));
                if (this.$product().product_state !== ProductStatus.active) {
                    this.form.controls.taxes.enable();
                    this.form.controls.surcharges_taxes.enable();
                    this.form.controls.surcharges.enable();
                    if (!this.$product().has_sales) {
                        this.form.controls.tax_mode.enable();
                    }
                } else {
                    this.form.controls.surcharges.disable();
                    this.form.controls.tax_mode.disable();
                }
            });
        });

    }

    ngOnDestroy(): void {
        this.#entitiesSrv.clearEntityTaxes();
        this.#productsSrv.product.surcharges.clear();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const requests$: Observable<void>[] = [];
            if (this.form.controls.surcharges.dirty) {
                const productSurcharges: ProductSurcharge[] = [];
                for (const [type] of this.surcharges) {
                    productSurcharges.push({
                        type,
                        ranges: cleanRangesBeforeSave(this.form.controls.surcharges.get([type, 'ranges']).value)
                    });
                }
                requests$.push(this.#productsSrv.product.surcharges.post(this.$product().product_id, productSurcharges));
            }
            if (this.form.controls.surcharges_taxes.dirty || this.form.controls.taxes.dirty || this.form.controls.tax_mode) {
                const updatedTaxes = {
                    tax_id: this.form.value.taxes,
                    surcharge_tax_id: this.form.value.surcharges_taxes,
                    settings: {
                        tax_mode: this.form.value.tax_mode
                    }
                };
                requests$.push(this.#productsSrv.product.update(this.$product().product_id, updatedTaxes));
            }
            return forkJoin(requests$).pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    private initMap(): void {
        this.surcharges = new Map();
        this.surcharges.set(this.surchargesTypes.generic, [{ from: 0, values: {} }]);
        this.surcharges.set(this.surchargesTypes.promotion, [{ from: 0, values: {} }]);
    }

    private reloadModels(): void {
        this.#productsSrv.product.load(this.$product().product_id);
        this.#productsSrv.product.surcharges.load(this.$product().product_id);
        this.form.markAsPristine();
    }
}
