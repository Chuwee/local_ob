import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    Product, ProductsService,
    ProductStatus,
    ProductStockType
} from '@admin-clients/cpanel/products/my-products/data-access';
import {
    CurrencyInputComponent,
    EphemeralMessageService,
    SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, shareReplay, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-product-simple',
    imports: [
        AsyncPipe, LocalCurrencyPipe, NgClass, MatMenuModule, MatCheckboxModule, MatTableModule, MatSelectModule, MatInputModule,
        MatFormFieldModule, MatTooltipModule, MatButtonModule, MatIconModule, TranslatePipe, ReactiveFormsModule, FormContainerComponent,
        FormControlErrorsComponent, CurrencyInputComponent, SearchablePaginatedSelectionModule, FormsModule
    ],
    templateUrl: './product-config-simple.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductConfigSimpleComponent implements OnInit {
    readonly #authSrv = inject(AuthenticationService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $product = input.required<Product>({ alias: 'product' });
    readonly $currency = computed(() => this.$product().currency_code);
    readonly productStates = ProductStatus;
    readonly productStockTypes = ProductStockType;

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#productsSrv.product.variants.loading$(),
        this.#productsSrv.product.inProgress$()
    ]);

    readonly form = this.#fb.group({
        sku: null as string,
        price: [null as number, [Validators.required, Validators.min(0)]],
        stock: [null as number, [Validators.required, Validators.min(0)]],
        id: null as number
    });

    readonly canWrite$ = this.#authSrv.getLoggedUser$().pipe(
        map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR])),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    constructor() {
        effect(() => {
            const product = this.$product();
            this.checkProductStatus(product);
        });
    }

    ngOnInit(): void {
        this.#productsSrv.product.variants.getData$()
            .pipe(
                filter(Boolean),
                tap(variants => {
                    const variant = variants[0];
                    if (variant) {
                        this.form.reset({
                            sku: variant.sku || '',
                            price: variant.price,
                            stock: variant.stock,
                            id: variant.id
                        }, { emitEvent: false });
                    }
                    this.checkProductStatus(this.$product());
                    this.form.markAsUntouched();
                    this.form.markAsPristine();
                }
                ),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe();

    }

    checkProductStatus(product: Product): void {
        if (product.product_state === ProductStatus.active) {
            this.form.get('price').disable({ emitEvent: false });
            this.form.get('stock').disable({ emitEvent: false });
        }
        if (product.product_state === ProductStatus.inactive) {
            this.form.get('price').enable({ emitEvent: false });
            this.form.get('stock').enable({ emitEvent: false });
        }
        if (product.stock_type === ProductStockType.unbounded) {
            this.form.get('stock').disable({ emitEvent: false });
        }
    }

    cancel(): void {
        this.reloadModels();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#productsSrv.product.variants.update(
                this.$product().product_id,
                this.form.controls.id.value,
                this.form.value
            ).pipe(tap(() => this.#ephemeralMessageSrv.showSaveSuccess()));
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    private reloadModels(): void {
        this.#productsSrv.product.variants.load(this.$product().product_id);
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

}

