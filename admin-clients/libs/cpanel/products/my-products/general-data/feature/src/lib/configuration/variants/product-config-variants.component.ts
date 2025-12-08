/* eslint-disable @typescript-eslint/dot-notation */
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    GetVariantsRequest, ProductStatus, ProductStockType, ProductVariant, ProductVariantStatus, ProductsService
} from '@admin-clients/cpanel/products/my-products/data-access';
import {
    CurrencyInputComponent, EphemeralMessageService, ObMatDialogConfig, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, KeyValuePipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, ElementRef, EventEmitter, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, forkJoin, map, Observable, shareReplay, startWith, switchMap, take, tap, throwError, withLatestFrom } from 'rxjs';
import { EditProductVariantsDialogComponent } from '../edit-product-variants/edit-product-variants-dialog.component';
import { EditVariantsPriceDialogComponent } from './edit-price-dialog/edit-variants-price-dialog.component';
import { ProductConfigVariantsFilterComponent } from './filter/config-variants-filter.component';

const MAX_VARIANTS = 25;
const PAGE_SIZE = 25;

@Component({
    selector: 'app-product-variants',
    imports: [
        AsyncPipe, LocalCurrencyPipe, KeyValuePipe, NgClass, MatMenuModule, MatCheckboxModule,
        MatTableModule, MatSelectModule, MatInputModule, MatFormFieldModule, MatTooltipModule,
        MatButtonModule, MatIconModule, TranslatePipe, ReactiveFormsModule, FormContainerComponent,
        FlexModule, FlexLayoutModule, CurrencyInputComponent, SearchablePaginatedSelectionModule, FormsModule,
        ProductConfigVariantsFilterComponent, ErrorMessage$Pipe, ErrorIconDirective, EllipsifyDirective
    ],
    templateUrl: './product-config-variants.component.html',
    styleUrls: ['./product-config-variants.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductConfigVariantsComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #elementRef = inject(ElementRef);

    readonly #columns = ['active', 'name', 'price', 'stock', 'sku', 'status'];
    #filters: GetVariantsRequest = { limit: PAGE_SIZE };
    readonly statusList = [ProductVariantStatus.active, ProductVariantStatus.inactive];

    readonly pageSize = PAGE_SIZE;
    readonly maxVariantsFromValuesCombination = MAX_VARIANTS;
    readonly showSelectedOnlyClick = new EventEmitter<boolean>();
    readonly allSelectedClick = new EventEmitter<boolean>();

    readonly form = this.#fb.group({});

    readonly #product$ = this.#productsSrv.product.get$().pipe(
        filter(Boolean),
        tap(product => {
            if (product.stock_type === ProductStockType.unbounded) {
                this.columns = this.#columns.filter(col => col !== 'stock');
            }
        }),
        takeUntilDestroyed(this.#destroyRef)
    );

    readonly #$product = toSignal(this.#product$);
    readonly $currency = computed(() => this.#$product().currency_code);

    readonly $productIsActive = computed(() => {
        if (this.#$product()?.product_state === ProductStatus.active) {
            this.formEnablerStatusHandler(ProductStatus.active);
            return true;
        } else if (this.#$product()?.product_state === ProductStatus.inactive) {
            this.formEnablerStatusHandler(ProductStatus.inactive);
        }
        return false;
    });

    readonly #variants$ = this.#productsSrv.product.variants.getData$().pipe(filter(Boolean));
    readonly $variants = toSignal(this.#variants$);

    readonly totalVariantsInTable$: Observable<number> = this.#productsSrv.product.variantsTable.getMetadata$()
        .pipe(map(metadata => metadata?.total || 0));

    readonly canWrite$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);

    readonly selectedOnly$: Observable<boolean> = this.showSelectedOnlyClick.pipe(
        startWith(false),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly allSelected$: Observable<boolean> = this.allSelectedClick.pipe(
        startWith(false),
        tap(isAllSelected => this.#isAllSelected = isAllSelected),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    #selectedVariants$: Observable<ProductVariant[]>;
    #isAllSelected: boolean;
    variantsTableList$: Observable<ProductVariant[]>;

    metadata$: Observable<Metadata>;
    selected = new FormControl([]);
    columns: string[] = this.#columns;

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#productsSrv.product.variants.loading$(),
        this.#productsSrv.product.variantsTable.loading$(),
        this.#productsSrv.product.inProgress$()
    ]);

    ngOnInit(): void {
        this.#productsSrv.product.load(this.#$product().product_id);
        this.#productsSrv.product.variants.loadIfNull(this.#$product().product_id);

        this.#productsSrv.product.variantsTable.getData$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(variants => {
                variants.forEach(variant => {
                    const variantGroup = this.#fb.group({
                        id: variant.id,
                        price: variant.price,
                        stock: this.#$product().stock_type === ProductStockType.bounded ? variant.stock : null,
                        sku: variant.sku || '',
                        status: variant.status
                    });
                    variantGroup.controls.price.addValidators([Validators.required, Validators.min(0)]);
                    if (this.#$product().stock_type === ProductStockType.bounded) {
                        variantGroup.controls.stock.addValidators([Validators.required, Validators.min(0)]);
                    }
                    this.form.setControl(variant.id.toString(), variantGroup);
                });
                if (this.#$product().product_state === ProductStatus.active) { this.formEnablerStatusHandler(ProductStatus.active); }
            });

        this.#selectedVariants$ = this.selected.valueChanges
            .pipe(
                map(selected => {
                    if (!selected || selected.length === 0) {
                        this.showSelectedOnlyClick.next(false);
                        return [];
                    }
                    return selected?.sort((a, b) => a.name.localeCompare(b.name));
                }),
                takeUntilDestroyed(this.#destroyRef),
                shareReplay({ bufferSize: 1, refCount: true })
            );

        this.#selectedVariants$.subscribe();

        // all selectable variants
        const allTableVariants$ = this.#productsSrv.product.variantsTable.getData$()
            .pipe(
                filter(Boolean),
                tap(variants => {
                    // Put variant values in selected form
                    this.selected.value?.forEach((selectedVariant, index) => {
                        const variantSelected = variants.find(variant => selectedVariant.id === variant.id);
                        if (variantSelected) {
                            this.selected.value[index] = variantSelected;
                        }
                    });
                }),
                shareReplay({ bufferSize: 1, refCount: true })
            );

        this.variantsTableList$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ? this.#selectedVariants$ : allTableVariants$),
            shareReplay(1)
        );

        this.metadata$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ?
                this.#selectedVariants$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this.#productsSrv.product.variantsTable.getMetadata$()
            ),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ bufferSize: 1, refCount: true })
        );
    }

    get selectedVariants(): number {
        return this.selected?.value?.length || 0;
    }

    clickShowSelected(): void {
        this.selectedOnly$.pipe(take(1)).subscribe((isSelected => this.showSelectedOnlyClick.emit(!isSelected)));
    }

    loadVariants(filters: Partial<GetVariantsRequest>): void {
        this.#filters = { ...this.#filters, ...filters };
        this.#productsSrv.product.variantsTable.loadTable(this.#$product().product_id, this.#filters);
        this.#productsSrv.product.variantsTable.getData$().pipe(
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnlyMode]) => this.showSelectedOnlyClick.emit(isSelectedOnlyMode));
    }

    editBulkPrices(): void {
        const selected = this.#isAllSelected ? 'allSelected' : this.selected.value;
        this.#matDialog.open(EditVariantsPriceDialogComponent, new ObMatDialogConfig(
            { currency: this.$currency(), productId: this.#$product().product_id, selected }
        ))
            .beforeClosed()
            .subscribe(saved => {
                if (saved) {
                    this.#ephemeralMessageSrv.showSaveSuccess();
                    this.allSelectedClick.emit(false);
                    this.selected.reset();
                    this.reloadModels();
                }
            });
    }

    openEditProductVariantsDialog(): void {
        this.#matDialog.open<EditProductVariantsDialogComponent, null, boolean>(
            EditProductVariantsDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(someActionDone => {
                if (someActionDone) {
                    this.#ephemeralMessageSrv.showSuccess({ msgKey: 'PRODUCT.VARIANTS.FORMS.FEEDBACKS.VARIANTS_EDITION_SUCCESS' });
                    this.loadVariants({ limit: PAGE_SIZE });
                }
            });
    }

    cancel(): void {
        this.reloadModels();
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            Object.keys(this.form.controls).forEach(variantId => {
                const variantCtrl = this.form.get(variantId);
                if (variantCtrl.dirty) {
                    obs$.push(this.#productsSrv.product.variants.update(this.#$product().product_id, +variantId, variantCtrl.value));
                }
            });
            return forkJoin(obs$).pipe(tap(() => this.#ephemeralMessageSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elementRef.nativeElement);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    private reloadModels(): void {
        this.#productsSrv.product.variantsTable.loadTable(this.#$product().product_id);
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    private formEnablerStatusHandler(productStatus: ProductStatus): void {
        if (productStatus === ProductStatus.active) {
            this.$variants()?.forEach(variant => {
                this.form.get([variant.id.toString(), 'price'])?.disable();
                this.form.get([variant.id.toString(), 'stock'])?.disable();
                this.form.get([variant.id.toString(), 'status'])?.disable();
            });
        }
        if (productStatus === ProductStatus.inactive) {
            this.$variants()?.forEach(variant => {
                this.form.get([variant.id.toString(), 'price'])?.enable();
                this.form.get([variant.id.toString(), 'stock'])?.enable();
                this.form.get([variant.id.toString(), 'status'])?.enable();
            });
        }
    }
}
