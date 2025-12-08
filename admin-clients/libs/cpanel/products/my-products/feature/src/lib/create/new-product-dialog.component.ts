import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    PostProduct, ProductFieldsRestriction, ProductsService, ProductStockType, ProductType
} from '@admin-clients/cpanel/products/my-products/data-access';
import { Producer, ProducersService, ProducerStatus } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService, EntitiesBaseState, EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrenciesFullTranslation$Pipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';
import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, first, map, shareReplay, switchMap, tap } from 'rxjs';

@Component({
    selector: 'app-new-product-dialog',
    imports: [
        SelectSearchComponent, TranslatePipe, ReactiveFormsModule, CommonModule, LayoutModule, FlexLayoutModule,
        MaterialModule, FormControlErrorsComponent, LocalCurrenciesFullTranslation$Pipe
    ],
    templateUrl: './new-product-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        EntitiesBaseService, EntitiesBaseState
    ]
})
export class NewProductDialogComponent implements OnInit, OnDestroy {
    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<NewProductDialogComponent>);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #producersService = inject(ProducersService);
    readonly #productsService = inject(ProductsService);
    readonly #elemRef = inject(ElementRef);
    readonly #ephemeralMsgService = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly producers$ = this.#producersService.getProducersListData$();
    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();

    readonly entities$ = combineLatest([
        this.#auth.getLoggedUser$().pipe(filter(Boolean)),
        this.canSelectEntity$
    ]).pipe(
        switchMap(([user, canSelectEntity]) => {
            if (canSelectEntity) {
                this.#entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [
                        EntitiesFilterFields.name,
                        EntitiesFilterFields.allowActivityEvents,
                        EntitiesFilterFields.allowAvetIntegration
                    ],
                    type: 'EVENT_ENTITY'
                });
                return this.#entitiesService.entityList.getData$();
            } else {
                this.#entitiesService.loadEntity(user.entity.id);
                return this.#entitiesService.getEntity$().pipe(
                    filter(Boolean),
                    map(entity => [entity])
                );
            }
        }),
        tap((entities: Entity[]) => {
            if (entities && entities.length === 1) {
                this.form.patchValue({ entity: entities[0] });
            }
        }),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    readonly isInProgress$ = booleanOrMerge([
        this.#entitiesService.entityList.inProgress$(),
        this.#entitiesService.isEntityLoading$(),
        this.#producersService.isProducersListLoading$()
    ]);

    readonly form = this.#fb.group({
        entity: [null as Entity, Validators.required],
        stock_type: [null as ProductStockType, Validators.required],
        producer: [{ value: null as Producer, disabled: true }, Validators.required],
        name: ['',
            [Validators.required,
            Validators.maxLength(ProductFieldsRestriction.productNameLength)]
        ],
        product_type: [null as ProductType, Validators.required],
        currency: [{ value: null as Currency, disabled: true }, Validators.required]
    });

    readonly stockTypes = ProductStockType;
    readonly productTypes = ProductType;

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.form.controls.entity.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(entity => {
                if (entity) {
                    this.form.controls.producer.enable();
                    this.#producersService.loadProducersList(
                        999,
                        0,
                        'name:asc',
                        '',
                        null,
                        entity.id,
                        [ProducerStatus.active]
                    );
                }
            });
        this.entities$.subscribe();

        this.form.controls.product_type.valueChanges.pipe(
            takeUntilDestroyed(this.#destroyRef),
            filter(type => type === this.productTypes.variant && this.form.controls.stock_type.value === this.stockTypes.session_bounded))
            .subscribe(() => {
                this.form.controls.stock_type.setValue(this.stockTypes.unbounded);
            });

        this.currencies$
            .pipe(first())
            .subscribe(currencies => {
                if (currencies?.length > 1) {
                    this.form.get('currency').enable({ emitEvent: false });
                } else {
                    this.form.get('currency').disable({ emitEvent: false });
                }
                this.form.get('currency').updateValueAndValidity();
            });
    }

    ngOnDestroy(): void {
        this.#entitiesService.entityList.clear();
        this.#entitiesService.clearEntity();
        this.#producersService.clearProducersList();
    }

    createProduct(): void {
        if (this.form.valid) {
            this.#auth.getLoggedUser$().pipe(first()).subscribe(user => {
                const product: PostProduct = {
                    name: this.form.value.name,
                    entity_id: this.form.value.entity.id,
                    producer_id: this.form.value.producer.id,
                    stock_type: this.form.value.stock_type,
                    product_type: this.form.value.product_type
                };
                const currencies = AuthenticationService.operatorCurrencyCodes(user);
                //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
                if (currencies?.length > 1) {
                    product.currency_code = this.form.value.currency.code;
                } else {
                    product.currency_code = currencies?.length === 1 ? currencies[0] : user.currency;
                }
                this.#productsService.product.create(product).subscribe(res => {
                    this.#ephemeralMsgService.showCreateSuccess();
                    this.close(res.id);
                });
            });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(productId: number = null): void {
        this.#dialogRef.close(productId);
    }

}
