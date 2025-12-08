import { Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    ACTIVITY_LIMITS_SERVICE, ActivityCapacity, ActivityCapacityValue, ActivityLimitsComponentService, ActivityLimitType,
    PriceTypeAvailability, VenueTemplateQuotaCapacity
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    ActivityTicketType, VenueTemplate, VenueTemplatePriceType, VenueTemplateQuota, VenueTemplatesService
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, Inject, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, take, takeUntil } from 'rxjs/operators';
import { PriceTypeRow } from './model/price-type-row.model';

const NOT_AVAILABLE_VALUE = '-';
const NOT_AVAILABLE_PLACEHOLDER = '-';

@Component({
    selector: 'app-activity-venue-template-limits',
    templateUrl: './activity-venue-template-limits.component.html',
    styleUrls: ['./activity-venue-template-limits.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, MatFormField, MatInput, MatIcon,
        MatTooltip, MatTableModule, MatButtonToggleModule, EllipsifyDirective
    ]
})
export class ActivityVenueTemplateLimitsComponent implements OnInit, OnDestroy {
    readonly #GLOBAL_PRICE_TYPE = 'global';
    readonly #onDestroy = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #translateService = inject(TranslateService);
    readonly #changeDetector = inject(ChangeDetectorRef);
    readonly #venueTemplateSrv = inject(VenueTemplatesService);
    readonly #formChange = new Subject<void>();

    #venueTemplate: VenueTemplate;
    #session: Session;
    @Input() form: FormGroup;

    priceTypesRows$: Observable<PriceTypeRow[]>;
    quotas$: Observable<VenueTemplateQuota[]>;
    columnNames: string[];
    ready$: Observable<boolean>;
    requestsInProgress$: Observable<boolean>;

    @Input() isSmartBooking: boolean = false;
    @Input() isSga: boolean = false;
    @Input() showVisibility: boolean = true;
    @Input()
    set venueTemplate(value: VenueTemplate) {
        this.#venueTemplate = value;
        if (value) this.loadQuotaCapacity();
    }

    @Input()
    set session(value: Session) {
        this.#session = value;
        if (value) this.loadQuotaCapacity();
    }

    constructor(
        @Inject(ACTIVITY_LIMITS_SERVICE) private _activityLimitsService: ActivityLimitsComponentService
    ) { }

    ngOnInit(): void {
        this.requestsInProgress$ = this._activityLimitsService.isActivityQuotaCapacityInProgress$();
        this.quotas$ = this.#venueTemplateSrv.getVenueTemplateQuotas$()
            .pipe(
                map(quotas => {
                    const result: VenueTemplateQuota[] = [];
                    if (quotas) {
                        result.push(quotas.find(quota => quota.default));
                        result.push(...quotas.filter(quota => !quota.default));
                        this.columnNames = result.map(quota => quota.name);
                    }
                    return result;
                }),
                shareReplay(1)
            );

        this.priceTypesRows$ = combineLatest([
            this.#venueTemplateSrv.getVenueTemplatePriceTypes$(),
            this.#venueTemplateSrv.getVenueTemplateQuotas$(),
            this._activityLimitsService.getActivityQuotaCapacity$()
        ]).pipe(
            map(([priceTypes, quotas, quotaCapacities]) => {
                if (priceTypes && quotas && quotaCapacities) {
                    const priceTypeRows = [
                        {
                            id: this.#GLOBAL_PRICE_TYPE,
                            name: this.#translateService.instant('VENUE_TPLS.TOTAL_CAPACITY'),
                            placeholders: {},
                            occupancy: null
                        } as PriceTypeRow,
                        ...priceTypes.map(priceType => ({ id: priceType.id, name: priceType.name, placeholders: {}, occupancy: null }))
                    ];
                    this.initForm(priceTypes, quotas, quotaCapacities, priceTypeRows);
                    return priceTypeRows;
                } else {
                    return [];
                }
            }),
            /* this way works fine (takeUntil and shareReplay in this order, reverse keeps limits component in memory),
             and takeUntil it's necessary to delete correctly the pipe, maybe some day this will be fixed, and takeUntil is unnecessary,
             shareReplay it's mandatory to simplify the initForm process */
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

        this.ready$ = combineLatest([
            this.#venueTemplateSrv.getVenueTemplatePriceTypes$(),
            this._activityLimitsService.getActivityQuotaCapacity$(),
            this.quotas$,
            this.priceTypesRows$.pipe(startWith(null as unknown[]))
        ]).pipe(map(sources => sources.every(source => !!source)));

        combineLatest([
            this.priceTypesRows$,
            this.quotas$,
            this._activityLimitsService.getPriceTypeAvailability$()
        ]).pipe(
            filter(sources => sources.every(source => source !== null)),
            takeUntilDestroyed(this.#onDestroy))
            .subscribe(([priceTypeRows, quotas, priceTypeAvailability]) =>
                this.updateOccupacies(priceTypeRows, quotas, priceTypeAvailability)
            );
    }

    ngOnDestroy(): void {
        this.#formChange.next();
        this.#formChange.complete();
        this._activityLimitsService.clearVenueTemplateQuotaCapacity();
    }

    reset(): void {
        this.#venueTemplateSrv.venueTpl.get$().pipe(
            first(Boolean)
        ).subscribe(() => {
            this._activityLimitsService.clearVenueTemplateQuotaCapacity();
            this.loadQuotaCapacity();
        });
    }

    save(): Observable<void> {
        return combineLatest([
            this.priceTypesRows$,
            this.quotas$
        ]).pipe(
            take(1),
            map(([priceTypes, quotas]) =>
                quotas.map(quota =>
                ({
                    id: quota.id,
                    price_types: priceTypes
                        .filter(priceType =>
                            priceType.id !== this.#GLOBAL_PRICE_TYPE
                            && this.getFormControl(priceType.id, quota.id).value !== NOT_AVAILABLE_VALUE
                        )
                        .map(priceType => {
                            const value = this.getFormControl(priceType.id, quota.id).value;
                            const visibilityValue = this.getVisibilityFormControl(priceType.id, quota.id).value;
                            return {
                                id: priceType.id,
                                capacity: {
                                    type: value === null && ActivityLimitType.unlimited || ActivityLimitType.fixed,
                                    value: value ?? undefined
                                },
                                on_sale: visibilityValue ?? null
                            } as ActivityCapacity;
                        }),
                    max_capacity: priceTypes
                        .filter(priceType => priceType.id === this.#GLOBAL_PRICE_TYPE)
                        .map(priceType => {
                            const value = this.getFormControl(priceType.id, quota.id).value;
                            return {
                                type: value === null && ActivityLimitType.unlimited || ActivityLimitType.fixed,
                                value: value ?? undefined
                            } as ActivityCapacityValue;
                        })
                        .find(maxCapcity => !!maxCapcity)
                })
                )
            ),
            switchMap(quotaCapacities =>
                this._activityLimitsService.updateActivityQuotaCapacity(
                    {
                        venueTemplateId: this.#venueTemplate?.id || this.#session?.venue_template?.id,
                        eventId: this.#session?.event?.id,
                        sessionId: this.#session?.id
                    }, quotaCapacities)
            )
        );
    }

    removeFromSale(priceTypeId: number | string, quotaId: number): void {
        const control = this.getVisibilityFormControl(priceTypeId, quotaId);
        control.setValue(!control.value);
        this.form.markAsDirty();
    }

    isOnSale(priceTypeId: number | string, quotaId: number): void {
        return this.getVisibilityFormControl(priceTypeId, quotaId).value;
    }

    getVisibilityFormControl(priceTypeId: number | string, quotaId: number): UntypedFormControl {
        return this.form.get([
            this.getPriceTypeVisibilityControlName(priceTypeId),
            this.getQuotaVisibilityControlName(quotaId)]
        ) as UntypedFormControl;
    }

    private loadQuotaCapacity(): void {
        this._activityLimitsService.loadActivityQuotaCapacity({
            venueTemplateId: this.#venueTemplate?.id || this.#session?.venue_template.id,
            eventId: this.#session?.event.id,
            sessionId: this.#session?.id
        });
    }

    private getFormControl(priceTypeId: number | string, quotaId: number): UntypedFormControl {
        return this.form.get([
            this.getPriceTypeControlName(priceTypeId),
            this.getQuotaControlName(quotaId)]
        ) as UntypedFormControl;
    }

    private getPriceTypeControlName(priceTypeId: number | string): string {
        return `priceType_${priceTypeId}`;
    }

    private getPriceTypeVisibilityControlName(priceTypeId: number | string): string {
        return `priceTypeVisibility_${priceTypeId}`;
    }

    private getQuotaControlName(quotaId: number | string): string {
        return `quota_${quotaId}`;
    }

    private getQuotaVisibilityControlName(quotaId: number | string): string {
        return `quota_${quotaId}_visibility`;
    }

    private initForm(
        priceTypes: VenueTemplatePriceType[],
        quotas: VenueTemplateQuota[],
        quotaCapacities: VenueTemplateQuotaCapacity[],
        priceTypeRows: PriceTypeRow[]
    ): void {
        this.#formChange.next();
        this.defineForm(priceTypes, quotas);
        this.setFormBehaviors(quotas, priceTypeRows);
        this.setFormValues(quotaCapacities);
    }

    private defineForm(priceTypes: VenueTemplatePriceType[], quotas: VenueTemplateQuota[]): void {
        Object.keys(this.form.controls).forEach(controlKey => this.form.removeControl(controlKey));
        this.form.setValue({});
        this.form.markAsPristine();
        if (this.isSmartBooking) {
            this.form.disable();
        }
        this.form.setControl(this.getPriceTypeControlName(this.#GLOBAL_PRICE_TYPE), this.getRowFormControl(quotas, this.form.disabled));
        priceTypes.forEach(priceType => {
            this.form.setControl(this.getPriceTypeControlName(priceType.id), this.getRowFormControl(quotas, this.form.disabled));
            this.form.setControl(
                this.getPriceTypeVisibilityControlName(priceType.id), this.getVisibilityRowFormControl(quotas, this.form.disabled)
            );
        });
    }

    private getRowFormControl(quotas: VenueTemplateQuota[], disable: boolean): UntypedFormGroup {
        const rowGroup = this.#fb.group({});
        quotas.forEach(quota => {
            rowGroup.addControl(
                this.getQuotaControlName(quota.id),
                this.#fb.control(
                    this.isSmartBooking ? NOT_AVAILABLE_VALUE : null,
                    { updateOn: 'blur' })
            );
        });
        if (disable) {
            rowGroup.disable();
        }
        return rowGroup;
    }

    private getVisibilityRowFormControl(quotas: VenueTemplateQuota[], disable: boolean): UntypedFormGroup {
        const rowGroup = this.#fb.group({});
        quotas.forEach(quota => {
            rowGroup.addControl(
                this.getQuotaVisibilityControlName(quota.id),
                this.#fb.control(null)
            );
        });
        if (disable) {
            rowGroup.disable();
        }
        return rowGroup;
    }

    private setFormBehaviors(
        quotas: VenueTemplateQuota[],
        priceTypeRows: PriceTypeRow[]
    ): void {
        let isChekingLimits = false;
        this.form.valueChanges.pipe(
            filter(() => !isChekingLimits),
            takeUntil(this.#formChange)
        ).subscribe(() => {
            isChekingLimits = true;
            this.checkLimits(quotas);
            this.refreshPlaceHolders(priceTypeRows, quotas);
            isChekingLimits = false;
        });
    }

    private checkLimits(quotas: VenueTemplateQuota[]): void {
        const defaultQuotaKey = this.getQuotaControlName(quotas.find(quota => quota.default).id);
        const globalPriceType = this.form.get(this.getPriceTypeControlName(this.#GLOBAL_PRICE_TYPE)) as UntypedFormGroup;
        // first row, quotas maximums, first cell as maximum limiting the other row cells
        if (globalPriceType) {
            this.checkPriceTypeLimits(globalPriceType, defaultQuotaKey);
            // quotas check, each cell must be equal or lower than top cell of the same column
            Object.keys(globalPriceType.value)
                .forEach(quotaKey => this.checkQuotasLimits(this.form, globalPriceType, quotaKey));
            // price type cells, each cell must be equal or lower than the default quota of the same price type
            Object.keys(this.form.value).forEach(priceTypeValueKey => {
                if (priceTypeValueKey !== this.getPriceTypeControlName(this.#GLOBAL_PRICE_TYPE)) {
                    this.checkPriceTypeLimits(this.form.get(priceTypeValueKey) as UntypedFormGroup, defaultQuotaKey);
                }
            });
        }
    }

    private checkPriceTypeLimits(priceTypeGroup: UntypedFormGroup, defaultQuotaKey: string): void {
        const totalCapacity = priceTypeGroup?.get(defaultQuotaKey)?.value;
        if (totalCapacity !== null) {
            Object.keys(priceTypeGroup.value).forEach(quotaValKey => {
                const currentValue = priceTypeGroup.value[quotaValKey];
                if (currentValue > totalCapacity) {
                    priceTypeGroup.get(quotaValKey).setValue(totalCapacity);
                }
            });
        }
    }

    private checkQuotasLimits(form: UntypedFormGroup, globalPriceTypeGroup: UntypedFormGroup, quotaKey: string): void {
        const quotaBase = globalPriceTypeGroup.get(quotaKey).value;
        if (quotaBase !== null) {
            Object.keys(form.value).forEach(priceTypeFormKey => {
                const currentValueControl = form.get(priceTypeFormKey).get(quotaKey);
                if (currentValueControl?.value > quotaBase) {
                    currentValueControl.setValue(quotaBase);
                }
            });
        }
    }

    private setFormValues(quotaCapacities: VenueTemplateQuotaCapacity[]): void {
        quotaCapacities.forEach(quotaCapacity => {
            this.getFormControl(this.#GLOBAL_PRICE_TYPE, quotaCapacity.id)?.setValue(this.convertApiValue(quotaCapacity.max_capacity));
            quotaCapacity.price_types.forEach(quotaPriceType => {
                this.getFormControl(quotaPriceType.id, quotaCapacity.id)?.setValue(this.convertApiValue(quotaPriceType.capacity));
                this.getVisibilityFormControl(quotaPriceType.id, quotaCapacity.id)?.setValue(quotaPriceType.on_sale ?? null);
            });
        });
    }

    private convertApiValue(capacity: ActivityCapacityValue): number {
        if (capacity?.type === ActivityLimitType.fixed) {
            return capacity.value;
        } else {
            return null;
        }
    }

    private refreshPlaceHolders(priceTypeRows: PriceTypeRow[], quotas: VenueTemplateQuota[]): void {
        const defaultQuotaKey = this.getQuotaControlName(quotas.find(quota => quota.default).id);
        const globalValueControlName = this.getPriceTypeControlName(this.#GLOBAL_PRICE_TYPE);
        const globalValuePlaceholder
            = this.form.get([globalValueControlName, defaultQuotaKey])?.value
            || this.#translateService.instant('FORMS.OPTIONS.UNLIMITED');
        // quotas limits map
        const quotasLimits: { [key: number]: number } = {};
        quotas.forEach(quota =>
            quotasLimits[quota.id] = this.form.get([globalValueControlName, this.getQuotaControlName(quota.id)])?.value);
        // price types limits map
        const priceTypesLimits: { [key: number]: number } = {};
        priceTypeRows.forEach(priceType =>
            priceTypesLimits[priceType.id] = this.form.get([this.getPriceTypeControlName(priceType.id), defaultQuotaKey])?.value);
        // starts the party
        priceTypeRows.forEach(priceType => {
            // first row is always conditioned by global limit
            if (priceType.id === this.#GLOBAL_PRICE_TYPE) {
                quotas.forEach(quota => priceType.placeholders[quota.id] = globalValuePlaceholder);
            } else {
                quotas.forEach(quota => {
                    // gets cell priceType and quota limit
                    const priceTypeLimit = priceTypesLimits[priceType.id];
                    const quotaLimit = quotasLimits[quota.id];
                    let cellPlaceholder;
                    // comparision logic, custom because nulls, always get more restricted, null is unrestricted
                    if (priceTypeLimit !== null) {
                        if (quotaLimit !== null && quotaLimit < priceTypeLimit) {
                            cellPlaceholder = quotaLimit;
                        } else {
                            cellPlaceholder = priceTypeLimit;
                        }
                    } else if (quotaLimit !== null) {
                        cellPlaceholder = quotaLimit;
                    } else {
                        cellPlaceholder = globalValuePlaceholder;
                    }
                    if (this.isSmartBooking && this.getFormControl(priceType.id, quota.id).value === NOT_AVAILABLE_VALUE) {
                        cellPlaceholder = NOT_AVAILABLE_PLACEHOLDER;
                    }
                    priceType.placeholders[quota.id] = cellPlaceholder;
                });
            }
        });
    }

    private updateOccupacies(
        priceTypeRows: PriceTypeRow[],
        quotas: VenueTemplateQuota[],
        priceTypeAvailabilities: PriceTypeAvailability[]
    ): void {
        const defaultQuotaId = quotas.find(quota => quota.default)?.id;
        priceTypeAvailabilities
            = priceTypeAvailabilities.filter(priceTypeAvailability => priceTypeAvailability.ticket_type === ActivityTicketType.individual);
        priceTypeRows
            .forEach(priceTypeRow => {
                priceTypeRow.occupancy = {};
                quotas.forEach(quota => priceTypeRow.occupancy[quota.id] = 0);
                priceTypeAvailabilities.forEach(priceTypeAvailability => {
                    // SOS
                    if (priceTypeRow.id === this.#GLOBAL_PRICE_TYPE) {
                        if (!priceTypeAvailability.price_type) {
                            if (!priceTypeAvailability.quota) {
                                priceTypeRow.occupancy[defaultQuotaId] += this.extractOccupacy(priceTypeAvailability);
                            } else {
                                priceTypeRow.occupancy[priceTypeAvailability.quota.id] += this.extractOccupacy(priceTypeAvailability);
                            }
                        }
                    } else if (priceTypeAvailability.price_type?.id === priceTypeRow.id) {
                        if (!priceTypeAvailability.quota) {
                            priceTypeRow.occupancy[defaultQuotaId] = this.extractOccupacy(priceTypeAvailability);
                        } else {
                            priceTypeRow.occupancy[priceTypeAvailability.quota.id] = this.extractOccupacy(priceTypeAvailability);
                        }
                    }
                });
            });
        this.#changeDetector.markForCheck();
    }

    private extractOccupacy(priceTypeAvailability: PriceTypeAvailability): number {
        return priceTypeAvailability.availability.purchase
            + priceTypeAvailability.availability.invitation
            + priceTypeAvailability.availability.issue
            + priceTypeAvailability.availability.booking;
    }
}
