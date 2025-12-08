import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BasePromotion, PromotionCollectiveScope } from '@admin-clients/cpanel/promoters/data-access';
import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl, ReactiveFormsModule, UntypedFormBuilder,
    UntypedFormGroup, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, map, Observable, Subject, takeUntil, tap, withLatestFrom } from 'rxjs';

@Component({
    selector: 'app-promotion-limits',
    templateUrl: './promotion-limits.component.html',
    styleUrls: ['./promotion-limits.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, NgIf, AsyncPipe, TranslatePipe,
        ReactiveFormsModule, FlexLayoutModule, FormControlErrorsComponent
    ]
})
export class PromotionLimitsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _isUserLimitsEnabled = new BehaviorSubject<boolean>(null);

    readonly promotionTypes = PromotionType;
    readonly promotionCollectiveScope = PromotionCollectiveScope;

    @Input() form: UntypedFormGroup;
    @Input() promotion$: Observable<BasePromotion>;

    isUserLimitsEnabled$ = this._isUserLimitsEnabled.asObservable();

    constructor(
        private _fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this.initForm();
        this.formChangesHandler();
        this.model();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private initForm(): void {
        this.form.addControl('limits', this._fb.group({
            packsMin: this._fb.group({
                enabled: { value: false, disabled: true }, // only non auto
                limit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
            }),
            purchaseMin: this._fb.group({
                enabled: { value: false, disabled: true }, // only non auto
                limit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
            }),
            promotionMax: this._fb.group({
                enabled: false,
                limit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
            }),
            eventUserMax: this._fb.group({
                enabled: { value: false, disabled: true }, // only non auto
                limit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
            }),
            sessionMax: this._fb.group({
                enabled: false,
                limit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
            }),
            sessionUserMax: this._fb.group({
                enabled: { value: false, disabled: true }, // only non auto
                limit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
            }),
            purchaseMax: this._fb.group({
                enabled: { value: false, disabled: true }, // only non auto
                limit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
            })
        }, { validators: this.validateLimits() }));
    }

    private model(): void {
        this.promotion$
            .pipe(
                takeUntil(this._onDestroy),
                filter(promo => !!promo),
                tap(promo => {
                    this.enableFieldsByPromoType(promo.type);
                    this.updateFormValues(promo);
                }),
                map(promo => promo.type !== this.promotionTypes.automatic && !!this.form.value.collective.collectiveId
                    && this.form.value.collective.type === this.promotionCollectiveScope.restricted)
            )
            .subscribe(enabled => this._isUserLimitsEnabled.next(enabled));
    }

    private formChangesHandler(): void {
        this.form.get('collective').valueChanges
            .pipe(
                takeUntil(this._onDestroy),
                withLatestFrom(this.promotion$),
                map(([{ type: collectiveType, collectiveId }, { type: promoType }]) =>
                    collectiveType === this.promotionCollectiveScope.restricted
                    && promoType !== this.promotionTypes.automatic && !!collectiveId
                )
            )
            .subscribe(enabled => this._isUserLimitsEnabled.next(enabled));

        this.form.get('limits.packsMin.enabled').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((active: boolean) => {
                if (active) {
                    this.form.get('limits.purchaseMin.enabled').reset();
                    this.form.get('limits.purchaseMin.limit').disable();
                }
            });

        this.form.get('limits.purchaseMin.enabled').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((active: boolean) => {
                if (active) {
                    this.form.get('limits.packsMin.enabled').reset();
                    this.form.get('limits.packsMin.limit').disable();
                }
            });

        this.form.get('limits').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(limits => {
                for (const prop in limits) {
                    const ctrl = this.form.get(`limits.${prop}.limit`);
                    if (limits[prop].enabled && ctrl.disabled) {
                        ctrl.enable({ emitEvent: false });
                        ctrl.markAsUntouched();
                    } else if (!limits[prop].enabled && ctrl.enabled) {
                        ctrl.disable({ emitEvent: false });
                        ctrl.markAsUntouched();
                    }
                }
            });

        combineLatest([
            this.promotion$,
            this.form.get('limits').valueChanges // only used as a trigger
        ])
            .pipe(
                takeUntil(this._onDestroy)
            )
            .subscribe(([promo]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.packsMin.enabled'),
                    promo.usage_limits.ticket_group_min?.enabled || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.packsMin.limit'),
                    promo.usage_limits.ticket_group_min?.limit || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.purchaseMin.enabled'),
                    promo.usage_limits.purchase_min?.enabled || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.purchaseMin.limit'),
                    promo.usage_limits.purchase_min?.limit || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.purchaseMax.enabled'),
                    promo.usage_limits.purchase_max?.enabled || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.purchaseMax.limit'),
                    promo.usage_limits.purchase_max?.limit || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.eventUserMax.enabled'),
                    promo.usage_limits.event_user_collective_max?.enabled || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.eventUserMax.limit'),
                    promo.usage_limits.event_user_collective_max?.limit || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.sessionUserMax.enabled'),
                    promo.usage_limits.session_user_collective_max?.enabled || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.sessionUserMax.limit'),
                    promo.usage_limits.session_user_collective_max?.limit || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.promotionMax.enabled'),
                    promo.usage_limits.promotion_max?.enabled || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.promotionMax.limit'),
                    promo.usage_limits.promotion_max?.limit || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.sessionMax.enabled'),
                    promo.usage_limits.session_max?.enabled || false
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('limits.sessionMax.limit'),
                    promo.usage_limits.session_max?.limit || false
                );
            });
    }

    private enableFieldsByPromoType(type: PromotionType): void {
        if (type === PromotionType.automatic) {
            this.form.get('limits.packsMin.enabled').disable();
            this.form.get('limits.purchaseMin.enabled').disable();
            this.form.get('limits.purchaseMax.enabled').disable();
            this.form.get('limits.eventUserMax.enabled').disable();
            this.form.get('limits.sessionUserMax.enabled').disable();
        } else {
            this.form.get('limits.packsMin.enabled').enable();
            this.form.get('limits.purchaseMin.enabled').enable();
            this.form.get('limits.purchaseMax.enabled').enable();
            this.form.get('limits.eventUserMax.enabled').enable();
            this.form.get('limits.sessionUserMax.enabled').enable();
        }
    }

    private updateFormValues(promotion: BasePromotion): void {
        this.form.patchValue({
            limits: {
                packsMin: promotion.usage_limits.ticket_group_min || { enabled: false, limit: null },
                purchaseMin: promotion.usage_limits.purchase_min || { enabled: false, limit: null },
                promotionMax: promotion.usage_limits.promotion_max || { enabled: false, limit: null },
                eventUserMax: promotion.usage_limits.event_user_collective_max || { enabled: false, limit: null },
                sessionMax: promotion.usage_limits.session_max || { enabled: false, limit: null },
                sessionUserMax: promotion.usage_limits.session_user_collective_max || { enabled: false, limit: null },
                purchaseMax: promotion.usage_limits.purchase_max || { enabled: false, limit: null }
            }
        });
        this.form.markAsPristine();
    }

    private validateLimits(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            const packsMin = control.get('packsMin') as UntypedFormGroup;
            const purchaseMin = control.get('purchaseMin') as UntypedFormGroup;
            const promotionMax = control.get('promotionMax') as UntypedFormGroup;
            const eventUserMax = control.get('eventUserMax') as UntypedFormGroup;
            const sessionMax = control.get('sessionMax') as UntypedFormGroup;
            const sessionUserMax = control.get('sessionUserMax') as UntypedFormGroup;
            const purchaseMax = control.get('purchaseMax') as UntypedFormGroup;

            const errors = {};

            const packsMinLimitsErrors = this.validatePacksMin(
                purchaseMax, promotionMax, sessionMax, packsMin, sessionUserMax, eventUserMax
            );
            const purchaseMinLimitsErrors = this.validatePurchaseMin(
                purchaseMin, purchaseMax, promotionMax, sessionMax, sessionUserMax, eventUserMax
            );
            const promotionMaxLimitsErrors = this.validatePromotionMax(purchaseMin, promotionMax, packsMin);
            const eventUserMaxLimitsErrors = this.validateEventUserMax(purchaseMin, promotionMax, packsMin, eventUserMax);
            const sessionMaxLimitsErrors = this.validateSessionMax(promotionMax, sessionMax, purchaseMin, packsMin, eventUserMax);
            const sessionUserMaxLimitsErrors = this.validateSessionUserMax(
                purchaseMin, promotionMax, sessionMax, packsMin, sessionUserMax, eventUserMax
            );
            const purchaseMaxLimitsErrors = this.validatePurchaseMax(
                purchaseMax, promotionMax, sessionMax, purchaseMin, packsMin, sessionUserMax, eventUserMax
            );

            const packsMinErrors = this.hasGenericErrors(packsMin.get('limit'))
                ? { ...packsMin.get('limit').errors } : { ...packsMinLimitsErrors };
            const purchaseMinErrors = this.hasGenericErrors(purchaseMin.get('limit'))
                ? { ...purchaseMin.get('limit').errors } : { ...purchaseMinLimitsErrors };
            const promotionMaxErrors = this.hasGenericErrors(promotionMax.get('limit'))
                ? { ...promotionMax.get('limit').errors } : { ...promotionMaxLimitsErrors };
            const eventUserMaxErrors = this.hasGenericErrors(eventUserMax.get('limit'))
                ? { ...eventUserMax.get('limit').errors } : { ...eventUserMaxLimitsErrors };
            const sessionMaxErrors = this.hasGenericErrors(sessionMax.get('limit'))
                ? { ...sessionMax.get('limit').errors } : { ...sessionMaxLimitsErrors };
            const sessionUserMaxErrors = this.hasGenericErrors(sessionUserMax.get('limit'))
                ? { ...sessionUserMax.get('limit').errors } : { ...sessionUserMaxLimitsErrors };
            const purchaseMaxErrors = this.hasGenericErrors(purchaseMax.get('limit'))
                ? { ...purchaseMax.get('limit').errors } : { ...purchaseMaxLimitsErrors };

            Object.assign(errors, packsMinLimitsErrors);
            packsMin.get('limit').setErrors(Object.keys(packsMinErrors).length ? packsMinErrors : null);
            Object.assign(errors, purchaseMinLimitsErrors);
            purchaseMin.get('limit').setErrors(Object.keys(purchaseMinErrors).length ? purchaseMinErrors : null);
            Object.assign(errors, promotionMaxLimitsErrors);
            promotionMax.get('limit').setErrors(Object.keys(promotionMaxErrors).length ? promotionMaxErrors : null);
            Object.assign(errors, eventUserMaxLimitsErrors);
            eventUserMax.get('limit').setErrors(Object.keys(eventUserMaxErrors).length ? eventUserMaxErrors : null);
            Object.assign(errors, sessionMaxLimitsErrors);
            sessionMax.get('limit').setErrors(Object.keys(sessionMaxErrors).length ? sessionMaxErrors : null);
            Object.assign(errors, sessionUserMaxLimitsErrors);
            sessionUserMax.get('limit').setErrors(Object.keys(sessionUserMaxErrors).length ? sessionUserMaxErrors : null);
            Object.assign(errors, purchaseMaxLimitsErrors);
            purchaseMax.get('limit').setErrors(Object.keys(purchaseMaxErrors).length ? purchaseMaxErrors : null);
            return Object.keys(errors).length ? errors : null;
        };
    }

    private hasGenericErrors(field: AbstractControl): boolean {
        // eslint-disable-next-line @typescript-eslint/dot-notation
        return !!field.errors?.['required'] || !!field.errors?.['min'];
    }

    private validatePacksMin(
        purchaseMax: UntypedFormGroup, promotionMax: UntypedFormGroup, sessionMax: UntypedFormGroup,
        packsMin: UntypedFormGroup, sessionUserMax: UntypedFormGroup, eventUserMax: UntypedFormGroup
    ): ValidationErrors | null {
        if (packsMin.value.enabled) {
            if (purchaseMax.enabled && purchaseMax.value.limit < packsMin.value.limit) {
                return { packsMinGTPurchaseMax: true };
            }
            if (promotionMax.enabled && promotionMax.value.limit < packsMin.value.limit) {
                return { packsMinGTPromotionMax: true };
            }
            if (sessionMax.enabled && sessionMax.value.limit < packsMin.value.limit) {
                return { packsMinGTSessionMax: true };
            }
            if (sessionUserMax.enabled && sessionUserMax.value.limit < packsMin.value.limit) {
                return { packsMinGTSessionUserMax: true };
            }
            if (eventUserMax.enabled && eventUserMax.value.limit < packsMin.value.limit) {
                return { packsMinGTEventUserMax: true };
            }
        }
        return null;
    }

    private validatePurchaseMin(
        purchaseMin: UntypedFormGroup, purchaseMax: UntypedFormGroup, promotionMax: UntypedFormGroup,
        sessionMax: UntypedFormGroup, sessionUserMax: UntypedFormGroup, eventUserMax: UntypedFormGroup
    ): ValidationErrors | null {
        if (purchaseMin.value.enabled) {
            if (promotionMax.value.enabled && promotionMax.value.limit < purchaseMin.value.limit) {
                return { purchaseMinGTPromotion: true };
            }
            if (sessionMax.value.enabled && sessionMax.value.limit < purchaseMin.value.limit) {
                return { purchaseMinGTSession: true };
            }
            if (purchaseMax.value.enabled && purchaseMax.value.limit < purchaseMin.value.limit) {
                return { purchaseMinGTPurchaseMax: true };
            }
            if (sessionUserMax.value.enabled && sessionUserMax.value.limit < purchaseMin.value.limit) {
                return { purchaseMinGTSessionUserMax: true };
            }
            if (eventUserMax.value.enabled && eventUserMax.value.limit < purchaseMin.value.limit) {
                return { purchaseMinGTEventUserMax: true };
            }
        }
        return null;
    }

    private validatePromotionMax(
        purchaseMin: UntypedFormGroup, promotionMax: UntypedFormGroup, packsMin: UntypedFormGroup
    ): ValidationErrors | null {
        if (promotionMax.enabled) {
            if (purchaseMin.value.enabled && promotionMax.value.limit >= purchaseMin.value.limit) {
                if (promotionMax.value.limit % purchaseMin.value.limit !== 0) {
                    return { purchaseMinNotPromotionMultiple: true };
                }
            }
            if (packsMin.value.enabled && promotionMax.value.limit >= packsMin.value.limit) {
                if (promotionMax.value.limit % packsMin.value.limit !== 0) {
                    return { packsMinNotPromotionMultiple: true };
                }
            }
        }
        return null;
    }

    private validateEventUserMax(
        purchaseMin: UntypedFormGroup, promotionMax: UntypedFormGroup, packsMin: UntypedFormGroup, eventUserMax: UntypedFormGroup
    ): ValidationErrors | null {
        if (eventUserMax.value.enabled) {
            if (promotionMax.value.enabled && promotionMax.value.limit < eventUserMax.value.limit) {
                return { eventUserMaxGTPromotion: true };
            }
            if (purchaseMin.value.enabled && eventUserMax.value.limit >= purchaseMin.value.limit) {
                if (eventUserMax.value.limit % purchaseMin.value.limit !== 0) {
                    return { purchaseMinNotEventUserMaxMultiple: true };
                }
            }
            if (packsMin.value.enabled && eventUserMax.value.limit >= packsMin.value.limit) {
                if (eventUserMax.value.limit % packsMin.value.limit !== 0) {
                    return { packsMinNotEventUserMaxMultiple: true };
                }
            }
        }
        return null;
    }

    private validateSessionMax(
        promotionMax: UntypedFormGroup, sessionMax: UntypedFormGroup, purchaseMin: UntypedFormGroup,
        packsMin: UntypedFormGroup, eventUserMax: UntypedFormGroup
    ): ValidationErrors | null {
        if (sessionMax.value.enabled) {
            if (promotionMax.value.enabled && promotionMax.value.limit < sessionMax.value.limit) {
                return { sessionGTPromotion: true };
            }
            if (eventUserMax.value.enabled && eventUserMax.value.limit < sessionMax.value.limit) {
                return { sessionGTEventUser: true };
            }
            //No mostrar este error si el valor de purchaseMin supera sessionMax
            if (purchaseMin.value.enabled && sessionMax.value.limit >= purchaseMin.value.limit) {
                if (sessionMax.value.limit % purchaseMin.value.limit !== 0) {
                    return { purchaseMinNotSessionMultiple: true };
                }
            }
            if (packsMin.value.enabled && sessionMax.value.limit >= packsMin.value.limit) {
                if (sessionMax.value.limit % packsMin.value.limit !== 0) {
                    return { packsMinNotSessionMultiple: true };
                }
            }
        }
        return null;
    }

    private validateSessionUserMax(
        purchaseMin: UntypedFormGroup, promotionMax: UntypedFormGroup, sessionMax: UntypedFormGroup,
        packsMin: UntypedFormGroup, sessionUserMax: UntypedFormGroup, eventUserMax: UntypedFormGroup
    ): ValidationErrors | null {
        if (sessionUserMax.value.enabled) {
            if (promotionMax.value.enabled && promotionMax.value.limit < sessionUserMax.value.limit) {
                return { sessionUserMaxGTPromotion: true };
            }
            if (sessionMax.value.enabled && sessionMax.value.limit < sessionUserMax.value.limit) {
                return { sessionUserMaxGTSession: true };
            }
            if (eventUserMax.value.enabled && eventUserMax.value.limit < sessionUserMax.value.limit) {
                return { sessionUserMaxGTEventUser: true };
            }
            if (purchaseMin.value.enabled && sessionUserMax.value.limit >= purchaseMin.value.limit) {
                if (sessionUserMax.value.limit % purchaseMin.value.limit !== 0) {
                    return { purchaseMinNotSessionUserMaxMultiple: true };
                }
            }
            if (packsMin.value.enabled && sessionUserMax.value.limit >= packsMin.value.limit) {
                if (sessionUserMax.value.limit % packsMin.value.limit !== 0) {
                    return { packsMinNotSessionUserMaxMultiple: true };
                }
            }
        }
        return null;
    }

    private validatePurchaseMax(
        purchaseMax: UntypedFormGroup, promotionMax: UntypedFormGroup, sessionMax: UntypedFormGroup, purchaseMin: UntypedFormGroup,
        packsMin: UntypedFormGroup, sessionUserMax: UntypedFormGroup, eventUserMax: UntypedFormGroup
    ): ValidationErrors | null {
        if (purchaseMax.value.enabled) {
            if (promotionMax.value.enabled && promotionMax.value.limit < purchaseMax.value.limit) {
                return { purchaseMaxGTPromotion: true };
            }
            if (sessionMax.value.enabled && sessionMax.value.limit < purchaseMax.value.limit) {
                return { purchaseMaxGTSession: true };
            }
            if (sessionUserMax.value.enabled && sessionUserMax.value.limit < purchaseMax.value.limit) {
                return { purchaseMaxGTSessionUser: true };
            }
            if (eventUserMax.value.enabled && eventUserMax.value.limit < purchaseMax.value.limit) {
                return { purchaseMaxGTEventUser: true };
            }
            //No mostrar este error si purchaseMin es mayor que purchaseMax
            if (purchaseMin.value.enabled && purchaseMax.value.limit >= purchaseMin.value.limit) {
                if (purchaseMax.value.limit % purchaseMin.value.limit !== 0) {
                    return { purchaseMinNotPurchaseMaxMultiple: true };
                }
            }
            if (packsMin.value.enabled && purchaseMax.value.limit >= packsMin.value.limit) {
                if (purchaseMax.value.limit % packsMin.value.limit !== 0) {
                    return { packsMinNotPurchaseMaxMultiple: true };
                }
            }
        }
        return null;
    }
}
