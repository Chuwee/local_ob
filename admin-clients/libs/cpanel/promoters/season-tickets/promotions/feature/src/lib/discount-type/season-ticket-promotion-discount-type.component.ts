import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketPromotion, SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { PromotionDiscountType, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import {
    CurrencyInputComponent,
    EphemeralMessageService,
    PercentageInputComponent,
    RangeTableComponent,
    resolveRanges
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { greaterThanValidator, nonZeroValidator, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    DestroyRef,
    inject,
    OnInit,
    QueryList,
    ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of } from 'rxjs';
import { catchError, filter, map, shareReplay } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        CurrencyInputComponent,
        LocalCurrencyPipe,
        FlexLayoutModule,
        PercentageInputComponent,
        LocalNumberPipe,
        RangeTableComponent,
        CommonModule
    ],
    selector: 'app-season-ticket-promotion-discount-type',
    templateUrl: './season-ticket-promotion-discount-type.component.html',
    styleUrls: ['./season-ticket-promotion-discount-type.component.scss']
})
export class SeasonTicketPromotionDiscountTypeComponent implements OnInit, WritingComponent {
    private readonly _stPromotionsSrv = inject(SeasonTicketPromotionsService);
    private readonly _stSrv = inject(SeasonTicketsService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _destroyRef = inject(DestroyRef);

    private _promotionId: number;
    private _seasonTicketId: number;
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly discountTypes = PromotionDiscountType;
    readonly promotionTypes = PromotionType;
    readonly reqInProgress$ = this._stPromotionsSrv.promotion.loading$();
    readonly currency$ = this._stSrv.seasonTicket.get$()
        .pipe(map(seasonTicket => seasonTicket.currency_code));

    readonly promotion$ = this._stPromotionsSrv.promotion.get$()
        .pipe(
            filter(promotion => !!promotion),
            takeUntilDestroyed(this._destroyRef),
            shareReplay(1)
        );

    readonly ranges$ = this.promotion$
        .pipe(
            map(promotion =>
                promotion.discount?.ranges?.map(range => ({ ...range, values: { fixed: range.value } })) || []
            )
        );

    readonly form = this._fb.group({
        type: [null, [Validators.required]],
        fixValue: [{ value: null, disabled: true }, [Validators.required, nonZeroValidator]],
        percentualValue: [
            { value: null, disabled: true },
            [Validators.required, greaterThanValidator(0), Validators.max(100)]
        ],
        ranges: this._fb.group({})
    });

    ngOnInit(): void {
        this.form.get('type').valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((discountType: PromotionDiscountType) => {
                if (discountType === PromotionDiscountType.fixed) {
                    this.form.get('fixValue').enable();
                    this.form.get('percentualValue').disable();
                } else if (discountType === PromotionDiscountType.percentage) {
                    this.form.get('fixValue').disable();
                    this.form.get('percentualValue').enable();
                } else {
                    this.form.get('fixValue').disable();
                    this.form.get('percentualValue').disable();
                }
            });

        combineLatest([
            this.promotion$,
            this.form.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promo]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('type'),
                    promo.discount.type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('fixValue'),
                    promo.discount.type === PromotionDiscountType.fixed ? promo.discount.value : null
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('percentualValue'),
                    promo.discount.type === PromotionDiscountType.percentage ? promo.discount.value : null
                );
            });

        combineLatest([
            this.promotion$,
            this._stSrv.seasonTicket.get$()
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promotion, seasonTicket]) => {
                this._seasonTicketId = seasonTicket.id;
                this._promotionId = promotion.id;
                this.updateForm(promotion.discount);
                if (promotion.discount.type) {
                    this.form.markAllAsTouched();
                } else {
                    this.form.markAsUntouched();
                }
                this.form.markAsPristine();
            });
    }

    cancel(): void {
        this.loadPromotion();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<boolean> {
        if (this.form.valid) {
            const data = this.form.value;
            const promotion: SeasonTicketPromotion = {
                discount: {
                    type: data.type,
                    value: data.type === PromotionDiscountType.fixed ?
                        data.fixValue : data.percentualValue,
                    ranges: data.type !== PromotionDiscountType.basePrice ?
                        null : resolveRanges(data)
                }
            };
            return this._stPromotionsSrv.promotion.update(this._seasonTicketId, this._promotionId, promotion)
                .pipe(
                    map(() => {
                        this._ephemeralMessageSrv.showSaveSuccess();
                        this.loadPromotion();
                        return true;
                    }),
                    catchError(() => of(false))
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return of(false);
        }
    }

    private updateForm(discountConfig: SeasonTicketPromotion['discount']): void {
        this.form.patchValue({
            type: discountConfig.type,
            fixValue: discountConfig.type === PromotionDiscountType.fixed ? discountConfig.value : null,
            percentualValue: discountConfig.type === PromotionDiscountType.percentage ? discountConfig.value : null
        });
    }

    private loadPromotion(): void {
        this._stPromotionsSrv.promotion.load(this._seasonTicketId, this._promotionId);
    }
}
