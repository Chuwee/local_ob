import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventPromotion, EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { PromotionDiscountType, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import {
    CurrencyInputComponent,
    DialogSize,
    EphemeralMessageService,
    MessageDialogService, PercentageInputComponent, RangeTableComponent, resolveRanges
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { greaterThanValidator, nonZeroValidator, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        ReactiveFormsModule,
        TranslatePipe,
        MaterialModule,
        FlexLayoutModule,
        CurrencyInputComponent,
        LocalCurrencyPipe,
        PercentageInputComponent,
        LocalNumberPipe,
        RangeTableComponent,
        ArchivedEventMgrComponent,
        CommonModule
    ],
    selector: 'app-event-promotion-discount-type',
    templateUrl: './event-promotion-discount-type.component.html',
    styleUrls: ['./event-promotion-discount-type.component.scss']
})
export class EventPromotionDiscountTypeComponent implements OnInit {
    private readonly _eventPromotionsSrv = inject(EventPromotionsService);
    private readonly _eventSrv = inject(EventsService);
    private readonly _fb = inject(FormBuilder);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _destroyRef = inject(DestroyRef);

    private _promotionId: number;
    private _eventId: number;
    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly currency$ = this._eventSrv.event.get$()
        .pipe(map(event => event.currency_code));

    readonly discountTypes = PromotionDiscountType;
    readonly promotionTypes = PromotionType;
    readonly reqInProgress$ = this._eventPromotionsSrv.promotion.loading$();
    readonly promotion$: Observable<EventPromotion> = this._eventPromotionsSrv.promotion.get$()
        .pipe(filter(Boolean));

    readonly ranges$ = this._eventPromotionsSrv.promotion.get$()
        .pipe(
            filter(Boolean),
            map(promotion =>
                promotion.discount?.ranges?.map(range => ({ ...range, values: { fixed: range.value } })) || []
            )
        );

    readonly form = this._fb.group({
        type: [null as PromotionDiscountType, [Validators.required]],
        fixValue: [{ value: null as number, disabled: true }, [Validators.required]],
        percentualValue: [
            { value: null as number, disabled: true },
            [Validators.required, greaterThanValidator(0), Validators.max(100)]
        ],
        ranges: this._fb.array([] as RangeElement[])
    });

    ngOnInit(): void {
        this.form.controls.type.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((discountType: PromotionDiscountType) => {
                if (discountType === PromotionDiscountType.fixed) {
                    this.form.controls.fixValue.enable();
                    this.form.controls.percentualValue.disable();
                } else if (discountType === PromotionDiscountType.percentage) {
                    this.form.controls.fixValue.disable();
                    this.form.controls.percentualValue.enable();
                } else {
                    this.form.controls.fixValue.disable();
                    this.form.controls.percentualValue.disable();
                }
            });

        combineLatest([
            this._eventPromotionsSrv.promotion.get$(),
            this.form.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promo]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.type,
                    promo.discount.type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.fixValue,
                    promo.discount.type === PromotionDiscountType.fixed ? promo.discount.value : null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.controls.percentualValue,
                    promo.discount.type === PromotionDiscountType.percentage ? promo.discount.value : null
                );
            });

        combineLatest([
            this.promotion$,
            this._eventSrv.event.get$()
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promotion, event]) => {
                this._eventId = event.id;
                this._promotionId = promotion.id;
                if (promotion.type !== PromotionType.automatic) {
                    this.form.controls.fixValue.removeValidators(nonZeroValidator);
                } else {
                    this.form.controls.fixValue.addValidators(nonZeroValidator);
                }

                this.form.patchValue({
                    type: promotion.discount.type,
                    fixValue: promotion.discount.type === PromotionDiscountType.fixed ? promotion.discount.value : null,
                    percentualValue: promotion.discount.type === PromotionDiscountType.percentage ? promotion.discount.value : null
                });

                if (promotion.discount.type) {
                    this.form.markAllAsTouched();
                } else {
                    this.form.markAsUntouched();
                }

                this.form.markAsPristine();
            });
    }

    cancel(): void {
        this._eventPromotionsSrv.promotion.load(this._eventId, this._promotionId);
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const data = this.form.getRawValue();
            const promotion: EventPromotion = {
                discount: {
                    type: data.type,
                    value: null,
                    ranges: null
                }
            };

            switch (data.type) {
                case this.discountTypes.fixed:
                    promotion.discount.value = data.fixValue;
                    break;
                case this.discountTypes.basePrice:
                    promotion.discount.ranges = resolveRanges(data);
                    promotion.discount.value = data.percentualValue;
                    break;
                case this.discountTypes.noDiscount:
                    promotion.discount.value = 0;
                    break;
                case this.discountTypes.percentage:
                    promotion.discount.value = data.percentualValue;
                    break;
            }

            const saveObs = this._eventPromotionsSrv.promotion.update(this._eventId, this._promotionId, promotion)
                .pipe(tap(() => {
                    this._ephemeralMsg.showSaveSuccess();
                    this._eventPromotionsSrv.promotion.load(this._eventId, this._promotionId);
                }));

            if (promotion.discount?.value === 0) {
                return this._msgDialogSrv.showWarn({
                    size: DialogSize.MEDIUM,
                    title: 'EVENTS.PROMOTIONS.DISCOUNT_TYPE_ZERO_WARNING_TITLE',
                    message: 'EVENTS.PROMOTIONS.DISCOUNT_TYPE_ZERO_WARNING_INFO'
                })
                    .pipe(
                        switchMap(confirmed => {
                            if (confirmed) {
                                return saveObs;
                            }
                            return throwError(() => 'zero value not accepted by user');
                        })
                    );
            }

            return saveObs;

        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return throwError(() => 'invalid fields');
        }
    }

    save(): void {
        this.save$().subscribe();
    }
}
