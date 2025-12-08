import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { productsDeliveryPointsProviders, ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import {
    ProductDeliveryPointsRelation, ProductDeliveryType, ProductDeliveryUnits, ProductsService
} from '@admin-clients/cpanel/products/my-products/data-access';
import { EphemeralMessageService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge, dateIsAfter, dateIsBefore, dateTimeValidator, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit,
    QueryList, ViewChildren, computed, inject
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MAT_DATE_FORMATS, MatOptionModule } from '@angular/material/core';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { localeData } from 'moment';
import { combineLatest, filter, first, forkJoin, map, Observable, tap, throwError, withLatestFrom } from 'rxjs';

const FORM_DEFAULT_VALUES = {
    delivery_data: {
        start_time_unit: null,
        start_time_value: 0,
        end_time_unit: null,
        end_time_value: 0,
        delivery_date_from: null,
        delivery_date_to: null,
        delivery_point_ids: []
    }
};

const PRODUCT_DP_RELATIONS_REQ = {
    limit: 999,
    offset: 0
};

const AVAILABLE_DELIVERY_POINTS_REQ = {
    limit: 999,
    offset: 0,
    status: 'ACTIVE',
    sort: 'name:asc'
};
@Component({
    selector: 'app-product-delivery',
    imports: [
        AsyncPipe, NgTemplateOutlet, RouterLink, MatRadioModule, MatFormFieldModule, MatSelectModule, MatDividerModule,
        MatIconModule, MatProgressSpinnerModule, MatOptionModule, MatInputModule, MatTooltipModule, TranslatePipe,
        ReactiveFormsModule, FormControlErrorsComponent, FormContainerComponent, SelectSearchComponent,
        MatDatepicker, MatDatepickerInput, MatIconButton, EllipsifyDirective
    ],
    templateUrl: './product-delivery.component.html',
    styleUrls: ['./product-delivery.component.scss'],
    providers: [productsDeliveryPointsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductDeliveryComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #productsSrv = inject(ProductsService);
    readonly #deliveryPointsSrv = inject(ProductsDeliveryPointsService);
    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #FORMATS = inject(MAT_DATE_FORMATS);

    readonly #$productId = toSignal(this.#productsSrv.product.get$().pipe(first(Boolean), map(product => product.product_id)));
    readonly #$entityId = toSignal(this.#productsSrv.product.get$().pipe(first(Boolean), map(product => product.entity.id)));

    readonly #getDeliveryPointsIdsFromRelation$ = this.#productsSrv.product.deliveryPointsRelationList.getData$().pipe(
        filter(Boolean),
        map(relations => this.mapProductDeliveryPointsRelationsToIds(relations))
    );

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly availableDeliveryPoints$ = this.#deliveryPointsSrv.productsDeliveryPointsList.getData$().pipe(first(Boolean));

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#productsSrv.product.delivery.loading$(),
        this.#productsSrv.product.deliveryPointsRelationList.loading$(),
        this.#deliveryPointsSrv.productsDeliveryPointsList.loading$()
    ]);

    readonly $isProductActive = toSignal(this.#productsSrv.product.get$().pipe(
        filter(Boolean),
        map(product => product.product_state === 'ACTIVE')
    ));

    readonly $canWrite = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]));

    readonly productDeliveryUnitsSession = [ProductDeliveryUnits.minutes, ProductDeliveryUnits.hours, ProductDeliveryUnits.days];
    readonly productDeliveryUnitsPurchase = [
        ProductDeliveryUnits.hours, ProductDeliveryUnits.days, ProductDeliveryUnits.weeks, ProductDeliveryUnits.months
    ];

    readonly dateFormat = localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();

    readonly form = this.#fb.group({
        delivery_type: [null as ProductDeliveryType, Validators.required],
        delivery_data: this.#fb.group({
            start_time_unit: [null as ProductDeliveryUnits, Validators.required],
            start_time_value: [null as number, [Validators.required, Validators.min(0)]],
            end_time_unit: [null as ProductDeliveryUnits, Validators.required],
            end_time_value: [null as number, [Validators.required, Validators.min(0)]],
            delivery_point_ids: [{ value: [] as number[], disabled: true }, Validators.required],
            delivery_date_from: [null as Date, [Validators.required, control => dateTimeValidator(
                dateIsBefore, 'startDateAfterEndDate', this.form?.value?.delivery_data.delivery_date_to)(control)
            ]],
            delivery_date_to: [null as Date, [Validators.required, control => dateTimeValidator(
                dateIsAfter, 'startDateAfterEndDate', this.form?.value?.delivery_data.delivery_date_from)(control)
            ]]
        })
    });

    readonly $sectionDisabled = computed(() => {
        const disableSection = this.$isProductActive() || !this.$canWrite();
        disableSection && this.form.disable();
        return disableSection;
    });

    ngOnInit(): void {
        this.#deliveryPointsSrv.productsDeliveryPointsList.load({ ...AVAILABLE_DELIVERY_POINTS_REQ, entityId: this.#$entityId() });
        this.#productsSrv.product.deliveryPointsRelationList.load(this.#$productId(), PRODUCT_DP_RELATIONS_REQ);

        combineLatest([
            this.#productsSrv.product.delivery.get$().pipe(filter(Boolean)),
            this.#getDeliveryPointsIdsFromRelation$
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([deliveryConfig, deliveryPointsRelations]) => {
                this.form.controls.delivery_type.patchValue(deliveryConfig.delivery_type);
                this.form.controls.delivery_data.patchValue({ ...deliveryConfig, delivery_point_ids: deliveryPointsRelations });
                if (deliveryConfig.delivery_date_to) {
                    this.form.controls.delivery_data.controls.delivery_date_to
                        .patchValue(this.setEndTime(deliveryConfig.delivery_date_to));
                }
                this.handleStatusFormFields(deliveryConfig.delivery_type);
            });

        this.handleDeliveryTypeChange();
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.deliveryPointsRelationList.clear();
        this.#deliveryPointsSrv.productsDeliveryPointsList.clear();
    }

    cancel(): void {
        this.reloadModels();
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const { delivery_point_ids: deliveryPointIds, ...productDeliveryReq } = this.form.controls.delivery_data.value;
            if (productDeliveryReq.delivery_date_to) {
                productDeliveryReq.delivery_date_to = this.setEndTime(productDeliveryReq.delivery_date_to);
            }
            const obs$: Observable<void>[] = [];
            const productDeliveryReq$ = this.#productsSrv.product.delivery.update(this.#$productId(), {
                ...productDeliveryReq,
                delivery_type: this.form.controls.delivery_type.value
            }).pipe(map(() => null));
            obs$.push(productDeliveryReq$);

            if (this.form.controls.delivery_type.value !== 'SESSION') {
                const productDeliveryPointsRelationsReq$ = this.#productsSrv.product.deliveryPointsRelationList.update(
                    this.#$productId(), { delivery_point_ids: deliveryPointIds }
                );
                obs$.push(productDeliveryPointsRelationsReq$);
            }
            return forkJoin(obs$).pipe(tap(() => this.#ephemeralMessageSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    private reloadModels(): void {
        this.#productsSrv.product.delivery.load(this.#$productId());
        this.#productsSrv.product.deliveryPointsRelationList.load(this.#$productId(), PRODUCT_DP_RELATIONS_REQ);
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    private handleDeliveryTypeChange(): void {
        this.form.controls.delivery_type.valueChanges
            .pipe(
                withLatestFrom(combineLatest([
                    // Don't use filter(Boolean), on a new product with empty delivery config we receive null, not empty object
                    this.#productsSrv.product.delivery.get$(),
                    this.#getDeliveryPointsIdsFromRelation$
                ])),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(([deliveryType, [deliveryConfig, deliveryPointsRelations]]) => {
                const initialValue = deliveryConfig && {
                    delivery_type: deliveryConfig.delivery_type,
                    delivery_data: {
                        ...deliveryConfig,
                        delivery_point_ids: deliveryPointsRelations
                    }
                };

                if (deliveryConfig && deliveryType && initialValue.delivery_type === deliveryType) {
                    this.form.patchValue({ ...initialValue }, { emitEvent: false });
                } else {
                    this.form.patchValue({ ...FORM_DEFAULT_VALUES }, { emitEvent: false });
                }

                if (deliveryConfig.delivery_date_to) {
                    this.form.controls.delivery_data.controls.delivery_date_to.patchValue(
                        this.setEndTime(deliveryConfig.delivery_date_to)
                    );
                }
                this.handleStatusFormFields(deliveryType);

                if (deliveryConfig) {
                    FormControlHandler.checkAndRefreshDirtyState(
                        this.form.controls.delivery_type,
                        initialValue.delivery_type
                    );
                }

                this.form.controls.delivery_data.markAsPristine();
                this.form.controls.delivery_data.markAsUntouched();
            });
    }

    private handleStatusFormFields(deliveryType: ProductDeliveryType): void {
        if (this.$canWrite() && !this.$isProductActive()) {
            const ctrls = this.form.controls.delivery_data.controls;
            if (deliveryType === 'PURCHASE' || deliveryType === 'FIXED_DATES') {
                ctrls.delivery_point_ids.enable({ emitEvent: false });
            } else {
                ctrls.delivery_point_ids.disable({ emitEvent: false });
            }
            if (deliveryType === 'FIXED_DATES') {
                ctrls.end_time_unit.disable({ emitEvent: false });
                ctrls.start_time_unit.disable({ emitEvent: false });
                ctrls.end_time_value.disable({ emitEvent: false });
                ctrls.start_time_value.disable({ emitEvent: false });
                ctrls.delivery_date_from.enable({ emitEvent: false });
                ctrls.delivery_date_to.enable({ emitEvent: false });
            } else if (deliveryType === 'PURCHASE' || deliveryType === 'SESSION') {
                ctrls.delivery_date_from.disable({ emitEvent: false });
                ctrls.delivery_date_to.disable({ emitEvent: false });
                ctrls.end_time_unit.enable({ emitEvent: false });
                ctrls.start_time_unit.enable({ emitEvent: false });
                ctrls.end_time_value.enable({ emitEvent: false });
                ctrls.start_time_value.enable({ emitEvent: false });
            }
        }
    }

    private mapProductDeliveryPointsRelationsToIds(relations: ProductDeliveryPointsRelation[]): number[] {
        return relations.map(relation => relation.delivery_point.id);
    }

    private setEndTime(date: string): Date {
        const endDate = new Date(date);
        endDate.setHours(23, 59, 59, 999);
        return endDate;
    }
}
