import {
    FormControlErrorsComponent,
    scrollIntoFirstInvalidFieldOrErrorMsg
} from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { PromotionTpl, PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { PromotionDiscountType, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import {
    CurrencyInputComponent, DialogSize, EphemeralMessageService, MessageDialogService, PercentageInputComponent,
    RangeTableComponent, resolveRanges
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyFullTranslationPipe, LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import {
    booleanOrMerge, greaterThanValidator, nonZeroValidator, FormControlHandler
} from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject, viewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, tap, throwError } from 'rxjs';
import { filter, first, map, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-promotion-tpl-discount-type',
    templateUrl: './promotion-tpl-discount-type.component.html',
    styleUrls: ['./promotion-tpl-discount-type.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [FormContainerComponent, ReactiveFormsModule, MaterialModule, FlexLayoutModule, TranslatePipe,
        CommonModule, LocalCurrencyPipe, CurrencyInputComponent, PercentageInputComponent,
        LocalNumberPipe, LocalCurrencyFullTranslationPipe, RangeTableComponent, FormControlErrorsComponent]
})
export class PromotionTplDiscountTypeComponent implements OnInit, WritingComponent {
    readonly #authSrv = inject(AuthenticationService);
    readonly #promotionTplsSrv = inject(PromotionTplsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    private _promotionId: number;
    private readonly _matExpansionPanels = viewChildren(MatExpansionPanel);

    readonly fixCurrencyBS = new BehaviorSubject<string>(null);
    readonly discountTypes = PromotionDiscountType;
    readonly promotionTypes = PromotionType;
    readonly rangeCurrencyBS = new BehaviorSubject<string>(null);
    readonly promotion$ = this.#promotionTplsSrv.getPromotionTemplate$().pipe(filter(Boolean));
    readonly reqInProgress$ = booleanOrMerge([
        this.#promotionTplsSrv.isPromotionTemplateLoading$(),
        this.#promotionTplsSrv.isPromotionTemplateSaving$()
    ]);

    readonly form = this.#fb.group({
        type: [null, Validators.required],
        fixValue: [{ value: null, disabled: true }, Validators.required],
        fixedCurrency: [{ value: null, disabled: true }, Validators.required],
        rangeCurrency: [{ value: null, disabled: true }, Validators.required],
        percentualValue: [
            { value: null, disabled: true },
            [Validators.required, greaterThanValidator(0), Validators.max(100)]
        ],
        ranges: this.#fb.group({})
    });

    readonly ranges$ = this.#promotionTplsSrv.getPromotionTemplate$()
        .pipe(
            filter(Boolean),
            map(promotion =>
                promotion.discount?.ranges?.map(range => ({ ...range, values: { fixed: range.value } })) || []
            )
        );

    userCurrencies: Currency[];

    ngOnInit(): void {
        this.#authSrv.getLoggedUser$()
            .pipe(first())
            .subscribe(user => this.userCurrencies = user.operator.currencies?.selected);

        this.form.get('fixedCurrency').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(currency => {
                this.fixCurrencyBS.next(currency);
                if (!(this.userCurrencies?.length > 1 && !this.fixCurrencyBS.value)) {
                    this.form.get('fixValue').enable({ emitEvent: false });
                }
            });

        this.form.get('rangeCurrency').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(currency => {
                if (this.rangeCurrencyBS.value) {
                    this.#msgDialogService.showWarn({
                        size: DialogSize.SMALL,
                        title: 'FORMS.INFOS.CHANGE_CURRENCY_WARN',
                        message: 'PROMOTIONS.FORMS.INFOS.CHANGE_CURRENCY_WARN_DETAILS',
                        actionLabel: 'FORMS.ACTIONS.OK',
                        showCancelButton: true
                    })
                        .subscribe(isConfirmed => {
                            if (isConfirmed) {
                                this.rangeCurrencyBS.next(currency);
                            } else {
                                this.form.get('rangeCurrency').setValue(this.rangeCurrencyBS.value, { emitEvent: false });
                            }
                        });
                } else {
                    this.rangeCurrencyBS.next(currency);
                }
            });

        this.form.get('type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((discountType: PromotionDiscountType) => {
                this.enablerForm(this.fixCurrencyBS.value, discountType);
            });

        combineLatest([
            this.#promotionTplsSrv.getPromotionTemplate$().pipe(filter(Boolean)),
            this.form.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
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

        this.#promotionTplsSrv.getPromotionTemplate$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(promotion => {
                this._promotionId = promotion.id;
                this.updateForm(promotion.discount);
                if (promotion.discount.type) {
                    this.form.markAllAsTouched();
                } else {
                    this.form.markAsUntouched();
                }
                if (promotion.type !== PromotionType.automatic) {
                    this.form.get('fixValue').removeValidators(nonZeroValidator);
                } else {
                    this.form.get('fixValue').addValidators(nonZeroValidator);
                }
                this.form.markAsPristine();
            });
    }

    cancel(): void {
        this.reload();
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            return this.#authSrv.getLoggedUser$()
                .pipe(
                    first(),
                    switchMap(user => {
                        const data = this.form.value;
                        const promotion: PromotionTpl = {
                            discount: {
                                type: data.type,
                                currency_code: null,
                                value: null,
                                ranges: null
                            }
                        };

                        switch (data.type) {
                            case this.discountTypes.fixed:
                                promotion.discount.value = data.fixValue;
                                promotion.discount.currency_code = data.fixedCurrency;
                                break;
                            case this.discountTypes.basePrice:
                                promotion.discount.ranges = resolveRanges(data);
                                promotion.discount.value = data.percentualValue;
                                promotion.discount.currency_code = data.rangeCurrency;
                                break;
                            case this.discountTypes.noDiscount:
                                promotion.discount.value = 0;
                                break;
                            case this.discountTypes.percentage:
                                promotion.discount.value = data.percentualValue;
                                promotion.discount.currency_code = data.rangeCurrency || data.fixedCurrency;
                                break;
                        }

                        if (!promotion.discount.currency_code) {
                            const currencies = AuthenticationService.operatorCurrencies(user);
                            promotion.discount.currency_code = currencies?.[0].code ?? user.currency;
                        }
                        if (promotion.discount?.value === 0) {
                            return this.showConfirmationZeroValue().pipe(
                                filter(accepted => accepted),
                                switchMap(() => this.savePromotion(promotion))
                            );
                        }

                        return this.savePromotion(promotion);
                    })
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue(), { emitEvent: false });
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels());
            return throwError(() => new Error('Invalid fields'));
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    private savePromotion(promotion: PromotionTpl): Observable<unknown> {
        return this.#promotionTplsSrv.savePromotionTemplate(this._promotionId, promotion)
            .pipe(tap(() => {
                this.#ephemeralMsg.showSaveSuccess();
                this.reload();
            }));
    }

    private updateForm(discountConfig: PromotionTpl['discount']): void {
        this.#authSrv.getLoggedUser$()
            .pipe(first())
            .subscribe(user => {
                this.form.reset({
                    type: discountConfig.type,
                    fixedCurrency: discountConfig.currency_code,
                    rangeCurrency: discountConfig.currency_code,
                    fixValue: discountConfig.type === PromotionDiscountType.fixed ? discountConfig.value : null,
                    percentualValue: discountConfig.type === PromotionDiscountType.percentage ? discountConfig.value : null
                }, { emitEvent: false });
                this.enablerForm(discountConfig.currency_code, discountConfig.type);

                const currencies = AuthenticationService.operatorCurrencyCodes(user);
                if (!discountConfig.currency_code && (!currencies || currencies.length === 1)) {
                    const currencyCode = currencies?.[0] ?? user.currency;
                    this.rangeCurrencyBS.next(currencyCode);
                    this.fixCurrencyBS.next(currencyCode);
                } else {
                    this.rangeCurrencyBS.next(discountConfig.currency_code);
                    this.fixCurrencyBS.next(discountConfig.currency_code);
                }
            });
    }

    private enablerForm(currencyCode: string, discountType: PromotionDiscountType): void {
        if (discountType === PromotionDiscountType.fixed) {
            if (!(this.userCurrencies?.length > 1 && !currencyCode)) {
                this.form.get('fixValue').enable({ emitEvent: false });
            }
            if (this.userCurrencies?.length > 1) {
                this.form.get('fixedCurrency').enable({ emitEvent: false });
            }
            this.form.get('percentualValue').disable({ emitEvent: false });
            this.form.get('rangeCurrency').disable({ emitEvent: false });
        } else if (discountType === PromotionDiscountType.percentage) {
            this.form.get('percentualValue').enable({ emitEvent: false });
            this.form.get('fixValue').disable({ emitEvent: false });
            this.form.get('fixedCurrency').disable({ emitEvent: false });
            this.form.get('rangeCurrency').disable({ emitEvent: false });
        } else {
            if (this.userCurrencies?.length > 1) {
                this.form.get('rangeCurrency').enable({ emitEvent: false });
            }
            this.form.get('fixValue').disable({ emitEvent: false });
            this.form.get('fixedCurrency').disable({ emitEvent: false });
            this.form.get('percentualValue').disable({ emitEvent: false });
        }
    }

    private reload(): void {
        this.form.markAsPending();
        this.form.markAsUntouched();
        this.#promotionTplsSrv.loadPromotionTemplate(this._promotionId);
    }

    private showConfirmationZeroValue(): Observable<boolean> {
        return this.#msgDialogService.showWarn({
            title: 'EVENTS.PROMOTIONS.DISCOUNT_TYPE_ZERO_WARNING_TITLE',
            message: 'EVENTS.PROMOTIONS.DISCOUNT_TYPE_ZERO_WARNING_INFO'
        }).pipe(map(Boolean));
    }
}
