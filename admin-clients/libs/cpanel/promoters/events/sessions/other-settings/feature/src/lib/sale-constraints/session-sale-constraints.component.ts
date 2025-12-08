import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, PriceTypeLimit, SaleConstraints } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { IsItalianComplianceEventPipe } from '@admin-clients/cpanel-promoters-events-utils';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe } from '@admin-clients/shared/utility/pipes';
import { maxDecimalLength } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, DestroyRef, effect, inject, Input, OnDestroy
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, ReactiveFormsModule, UntypedFormArray, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';

const customerLimitsTypes = {
    session: 'session',
    priceZone: 'price_zone'
} as const;

type CustomerLimitType = typeof customerLimitsTypes[keyof typeof customerLimitsTypes];

@Component({
    selector: 'app-session-sale-constraints',
    imports: [
        ReactiveFormsModule, TranslatePipe, MatFormFieldModule, MatCheckboxModule, MatTableModule, MatRadioGroup, MatRadioButton,
        MatInputModule, MatIconModule, MatButtonModule, ErrorMessage$Pipe, AsyncPipe, ErrorIconDirective, MatDividerModule, MatSelectModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['session-sale-constraints.component.scss'],
    templateUrl: 'session-sale-constraints.component.html'
})
export class SessionSaleConstraintsComponent implements OnDestroy {
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #isItalianCompliancePipe = inject(IsItalianComplianceEventPipe);
    readonly #eventsSrv = inject(EventsService);

    readonly priceZoneLimitTableHead = ['price_zone', 'min', 'max'];
    readonly priceTypes$ = this.#venueTemplatesService.getVenueTemplatePriceTypes$().pipe(filter(priceTypes => !!priceTypes));

    readonly saleConstraintsGroup = this.#fb.group({
        enableCartTicketsLimit: false,
        cartTicketsLimit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
        enablePriceGroupLimits: { value: false, disabled: true },
        priceGroupLimitsMatrix: this.#fb.array([]),
        customers_limits_enabled: false,
        customer_limits_type: this.#fb.nonNullable.control<CustomerLimitType>(customerLimitsTypes.session),
        customerTicketsLimit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
        price_type_limits: this.#fb.array<PriceTypeLimit[]>([], { validators: Validators.required })
    });

    readonly $availableCustomerPriceTypes = toSignal(combineLatest([
        this.priceTypes$,
        this.saleConstraintsGroup.get('price_type_limits').valueChanges
    ])
        .pipe(map(([priceTypes, formValues]) => priceTypes?.map(pt => ({
            ...pt,
            disabled: formValues?.some((formPt: { price_type_id?: number }) => formPt.price_type_id === pt.id)
        }))))
    );

    readonly $allowAddCustomerPriceTypeLimit = computed(() => !this.$availableCustomerPriceTypes()?.every(pt => pt.disabled));

    readonly $session = toSignal(this.#sessionsSrv.session.get$().pipe(filter(session => !!session)));
    readonly $event = toSignal(this.#eventsSrv.event.get$().pipe(filter(event => !!event)));

    // TODO: En el futuro, debería de venir el número directamente asociado al evento / entidad
    readonly $maxCustomerTicketsLimit = computed(() => this.#isItalianCompliancePipe.transform(this.$event()) ? 10 : null);

    @Input() set form(value: UntypedFormGroup) {
        if (!value.contains('saleConstraints')) {
            value.addControl('saleConstraints', this.saleConstraintsGroup, { emitEvent: false });
        }
    }

    get customerPriceTypeLimits(): FormArray { return this.saleConstraintsGroup.get('price_type_limits') as FormArray; };
    get priceGroupLimitsMatrix(): UntypedFormArray {
        return this.saleConstraintsGroup.get('priceGroupLimitsMatrix') as UntypedFormArray;
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    constructor() {
        this.#saleConstraintsFormChangeHandler();
        this.#updateSaleConstraintForm();

        effect(() => {
            if (this.#isItalianCompliancePipe.transform(this.$event())) {
                this.saleConstraintsGroup.controls.customers_limits_enabled?.disable();
                this.saleConstraintsGroup.controls.customer_limits_type?.disable();
            }
        });

        effect(() => {
            const session = this.$session();
            if (session) {
                this.#venueTemplatesService.clearVenueTemplatePriceTypes();
                this.#sessionsSrv.clearSaleConstraints();
                this.#venueTemplatesService.loadVenueTemplatePriceTypes(session.venue_template.id);
                this.#sessionsSrv.loadSaleConstraints(session.event.id, session.id);
            }
        });
    }

    ngOnDestroy(): void {
        const form = this.saleConstraintsGroup.parent as UntypedFormGroup;
        form.removeControl('saleConstraints', { emitEvent: false });
    }

    getSaleConstraintsRequest(priceTypes: VenueTemplatePriceType[], eventId: number, sessionId: number): Observable<void>[] {
        const requests: Observable<void>[] = [];
        if (this.saleConstraintsGroup.dirty) {
            const constraintsGroupData = this.saleConstraintsGroup.getRawValue();
            const saleConstraints = {
                cart_limits_enabled: constraintsGroupData.enableCartTicketsLimit,
                cart_limits: {
                    price_type_limits_enabled: constraintsGroupData.enablePriceGroupLimits,
                    limit: constraintsGroupData.cartTicketsLimit,
                    price_type_limits: (constraintsGroupData.priceGroupLimitsMatrix as SaleConstraints['cart_limits']['price_type_limits'])
                        ?.map((priceGroupLimit, i) => ({ ...priceGroupLimit, price_type_id: priceTypes[i].id }))
                },
                customers_limits_enabled: constraintsGroupData.customers_limits_enabled,
                customers_limits: constraintsGroupData.customer_limits_type === customerLimitsTypes.session ? {
                    max: constraintsGroupData.customerTicketsLimit
                } : {
                    price_type_limits: constraintsGroupData.price_type_limits
                }
            };
            requests.push(
                this.#sessionsSrv.updateSaleConstraints(eventId, sessionId, saleConstraints)
            );
        }
        return requests;
    }

    addCustomerPriceTypeLimit(priceZone: number = null, maxLimit: number = null): void {
        const priceLimitGroup = this.#fb.group({
            price_type_id: [priceZone, [Validators.required]],
            max: [maxLimit, [Validators.required, Validators.min(1), maxDecimalLength(0)]]
        });

        this.customerPriceTypeLimits.push(priceLimitGroup);
    }

    deleteCustomerPriceTypeLimit(index: number): void {
        if (this.customerPriceTypeLimits.length < 2) this.customerPriceTypeLimits.at(index).reset();
        else this.customerPriceTypeLimits.removeAt(index);
        this.customerPriceTypeLimits.markAsDirty();
    }

    #saleConstraintsFormChangeHandler(): void {
        this.saleConstraintsGroup.get('enableCartTicketsLimit').valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(isEnabled => {
                const cartTicketsLimit = this.saleConstraintsGroup.get('cartTicketsLimit');
                const enablePriceGroupLimits = this.saleConstraintsGroup.get('enablePriceGroupLimits');
                if (isEnabled) {
                    cartTicketsLimit.enable({ emitEvent: false });
                    enablePriceGroupLimits.enable({ emitEvent: false });
                } else {
                    cartTicketsLimit.disable({ emitEvent: false });
                    enablePriceGroupLimits.setValue(false);
                    enablePriceGroupLimits.markAsDirty();
                    enablePriceGroupLimits.disable({ emitEvent: false });
                }
            });

        this.saleConstraintsGroup.get('cartTicketsLimit').valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(cartTicketsLimit => {
                this.priceTypes$
                    .pipe(first())
                    .subscribe(priceTypes => {
                        priceTypes?.length && priceTypes.forEach((_, index) => {
                            const priceZoneMinMax = this.priceGroupLimitsMatrix.at(index) as UntypedFormGroup;
                            priceZoneMinMax?.get('max').setValue(cartTicketsLimit);
                            priceZoneMinMax?.get('min').updateValueAndValidity();
                        });
                    });
            });

        this.saleConstraintsGroup.get('enablePriceGroupLimits').valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(isEnabled => {
                if (isEnabled) {
                    this.priceGroupLimitsMatrix.enable({ emitEvent: false });
                } else {
                    this.priceGroupLimitsMatrix.disable({ emitEvent: false });
                }
            });

        this.saleConstraintsGroup?.get('customers_limits_enabled').valueChanges.pipe(
            takeUntilDestroyed()
        ).subscribe((isEnabled: boolean) => {
            if (isEnabled) {
                if (this.saleConstraintsGroup.get('customer_limits_type').value === customerLimitsTypes.session) {
                    this.saleConstraintsGroup.get('customerTicketsLimit').enable({ emitEvent: false });
                    this.saleConstraintsGroup.get('price_type_limits').disable({ emitEvent: false });
                } else {
                    this.saleConstraintsGroup.get('price_type_limits').enable({ emitEvent: false });
                    if (!(this.saleConstraintsGroup.get('price_type_limits') as FormArray).length) {
                        this.addCustomerPriceTypeLimit();
                    }
                    this.saleConstraintsGroup.get('customerTicketsLimit').disable({ emitEvent: false });
                }
            } else {
                this.saleConstraintsGroup.get('price_type_limits').disable({ emitEvent: false });
                this.saleConstraintsGroup.get('customerTicketsLimit').disable({ emitEvent: false });
            }
        });

        this.saleConstraintsGroup?.get('customer_limits_type').valueChanges.pipe(
            takeUntilDestroyed()
        ).subscribe((customerType: CustomerLimitType) => {
            if (customerType === customerLimitsTypes.session) {
                this.saleConstraintsGroup.get('customerTicketsLimit').enable({ emitEvent: false });
                this.saleConstraintsGroup.get('customerTicketsLimit').markAsUntouched();
                this.saleConstraintsGroup.get('price_type_limits').disable({ emitEvent: false });
                this.saleConstraintsGroup.get('price_type_limits').reset();
            } else {
                this.saleConstraintsGroup.get('price_type_limits').enable({ emitEvent: false });
                if (!(this.saleConstraintsGroup.get('price_type_limits') as FormArray).length) {
                    this.addCustomerPriceTypeLimit();
                }
                this.saleConstraintsGroup.get('customerTicketsLimit').disable({ emitEvent: false });
                this.saleConstraintsGroup.get('customerTicketsLimit').reset();
                this.saleConstraintsGroup.get('price_type_limits').markAsUntouched();
            }
        });
    }

    #updateSaleConstraintForm(): void {
        this.priceTypes$
            .pipe(takeUntilDestroyed())
            .subscribe(priceTypes => {
                this.#addPriceGroupLimitsControls(priceTypes);
            });

        combineLatest([
            this.#sessionsSrv.getSaleConstraints(),
            this.priceTypes$
        ]).pipe(
            takeUntilDestroyed()
        ).subscribe(([saleConstraints, priceTypes]) => {
            const priceGroupLimits = priceTypes.map(priceType => {
                const currentPriceTypeLimits = saleConstraints?.cart_limits?.price_type_limits
                    ?.find(ptLimit => ptLimit.price_type_id === priceType.id);

                return {
                    min: currentPriceTypeLimits?.min || 0,
                    max: currentPriceTypeLimits?.max || saleConstraints?.cart_limits?.limit || 1
                };
            });
            this.saleConstraintsGroup.patchValue({
                enableCartTicketsLimit: saleConstraints?.cart_limits_enabled,
                enablePriceGroupLimits: saleConstraints?.cart_limits?.price_type_limits_enabled,
                priceGroupLimitsMatrix: priceGroupLimits,
                customers_limits_enabled: saleConstraints?.customers_limits_enabled || false
            }, { onlySelf: true });
            this.saleConstraintsGroup.patchValue({
                cartTicketsLimit: saleConstraints?.cart_limits?.limit
            }, { emitEvent: false });

            (this.saleConstraintsGroup.get('price_type_limits') as FormArray).clear();

            const limits = saleConstraints?.customers_limits;
            if (limits) {
                if ('price_type_limits' in limits && limits.price_type_limits.length) {
                    limits.price_type_limits.forEach(ptLimit => {
                        this.addCustomerPriceTypeLimit(ptLimit.price_type_id, ptLimit.max);
                    });
                    this.saleConstraintsGroup.get('customer_limits_type').setValue(customerLimitsTypes.priceZone);
                } else if ('max' in limits) {
                    this.saleConstraintsGroup.get('customerTicketsLimit').setValue(limits.max);
                }
            }
            this.saleConstraintsGroup.markAsPristine();
            this.saleConstraintsGroup.markAsUntouched();
        });
    }

    #addPriceGroupLimitsControls(priceTypes: VenueTemplatePriceType[]): void {
        this.priceGroupLimitsMatrix.clear();
        const cartTicketsLimitCtrl = this.saleConstraintsGroup.get('cartTicketsLimit');
        for (let i = 0; i < priceTypes.length; i++) {
            const fGroup = this.#fb.group({});
            const min = this.#fb.control(0);
            const max = this.#fb.control(1);
            min.setValidators([
                Validators.required,
                Validators.min(0),
                maxDecimalLength(0),
                control => Validators.max(Math.min(max.value, cartTicketsLimitCtrl.value))(control)
            ]);
            max.setValidators([
                Validators.required,
                maxDecimalLength(0),
                control => Validators.min(Math.max(1, min.value))(control),
                control => Validators.max(cartTicketsLimitCtrl.value || 1)(control)
            ]);
            fGroup.addControl('min', min);
            fGroup.addControl('max', max);

            this.#destroyRef.onDestroy(() => {
                min.setValidators(null);
                max.setValidators(null);
                min.updateValueAndValidity({ emitEvent: false });
                max.updateValueAndValidity({ emitEvent: false });
            });
            this.priceGroupLimitsMatrix.push(fGroup);
        }
    }

}
