import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import {
    ChannelPromotionsService, ChannelPromotionDiscountType, ChannelPromotionType, ChannelPromotion
} from '@admin-clients/cpanel-channels-promotions-data-access';
import {
    CurrencyInputComponent, DialogSize, EphemeralMessageService, MessageDialogService, PercentageInputComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import {
    ErrorMessage$Pipe, LocalCurrencyPartialTranslationPipe, LocalCurrencyPipe, LocalNumberPipe
} from '@admin-clients/shared/utility/pipes';
import { greaterThanValidator } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, OnInit, QueryList, ViewChildren, inject, DestroyRef, signal
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, of, skip, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-channel-promotion-discount-type',
    templateUrl: './channel-promotion-discount-type.component.html',
    styleUrls: ['./channel-promotion-discount-type.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, CurrencyInputComponent, MaterialModule,
        FlexLayoutModule, LocalCurrencyPipe, TranslatePipe, CommonModule, LocalCurrencyPartialTranslationPipe,
        PercentageInputComponent, LocalNumberPipe, ErrorIconDirective, ErrorMessage$Pipe
    ]
})
export class ChannelPromotionDiscountTypeComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #authSrv = inject(AuthenticationService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #channelSrv = inject(ChannelsService);
    readonly #channelPromotionSrv = inject(ChannelPromotionsService);
    readonly #messageDialogSrv = inject(MessageDialogService);

    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly #isMultiCurrency$ = isMultiCurrency$().pipe(shareReplay({ bufferSize: 1, refCount: true }));

    #promotionId: number;
    #channelId: number;
    #isMultiCurrency: boolean;
    readonly #hasMoreThanOneCurrencyBS = new BehaviorSubject<boolean>(false);

    readonly discountTypes = ChannelPromotionDiscountType;
    readonly promotionTypes = ChannelPromotionType;
    readonly currencies$ = this.#channelSrv.getChannel$()
        .pipe(
            first(),
            withLatestFrom(this.#authSrv.getLoggedUser$().pipe(first(Boolean))),
            tap(([channel]) => this.#hasMoreThanOneCurrencyBS.next(channel.currencies?.length > 1)),
            map(([channel, user]) => {
                if (channel.currencies) {
                    return channel.currencies.map(curr => curr.code);
                } else {
                    return [user?.currency];
                }
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly loading$ = this.#channelPromotionSrv.isPromotionInProgress$();
    readonly promotion$ = this.#channelPromotionSrv.getPromotion$()
        .pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly form = this.#fb.group({
        type: [null, [Validators.required]],
        fixValue: [{ value: null, disabled: true }, [Validators.required, greaterThanValidator(0)]],
        fixedValues: this.#fb.array([{ value: null, disabled: true }]),
        percentualValue: [
            { value: null, disabled: true },
            [Validators.required, greaterThanValidator(0), Validators.max(100)]
        ]
    });

    readonly $dataSource = signal([{}]);

    ngOnInit(): void {
        this.form.get('type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((discountType: ChannelPromotionDiscountType) => {
                if (discountType === ChannelPromotionDiscountType.fixed && !this.#hasMoreThanOneCurrencyBS.value) {
                    this.form.get('fixValue').enable();
                    this.form.get('fixedValues').disable();
                    this.form.get('percentualValue').disable();
                } else if (discountType === ChannelPromotionDiscountType.fixed && this.#hasMoreThanOneCurrencyBS.value) {
                    this.form.get('fixedValues').enable();
                    this.form.get('fixValue').disable();
                    this.form.get('percentualValue').disable();
                } else if (discountType === ChannelPromotionDiscountType.percentage) {
                    this.form.get('percentualValue').enable();
                    this.form.get('fixValue').disable();
                    this.form.get('fixedValues').disable();
                } else {
                    this.form.get('fixValue').disable();
                    this.form.get('fixedValues').disable();
                    this.form.get('percentualValue').disable();
                }
            });

        combineLatest([
            this.promotion$,
            this.#channelSrv.getChannel$(),
            this.#isMultiCurrency$
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([promotion, channel, isMultiCurrency]) => {
                this.#channelId = channel.id;
                this.#promotionId = promotion.id;
                this.#isMultiCurrency = isMultiCurrency;
                const originalData = promotion.discount;
                this.form.patchValue({
                    type: originalData?.type,
                    fixValue: originalData?.type === ChannelPromotionDiscountType.fixed ? originalData.value : null,
                    fixedValues: originalData.type === ChannelPromotionDiscountType.fixed ? originalData.fixed_values : null,
                    percentualValue: originalData?.type === ChannelPromotionDiscountType.percentage ? originalData.value : null
                });
                //TODO MULTICURRENCY: clean and simplify code when all is multicurrency
                if (isMultiCurrency) {
                    const fixedValues = originalData?.fixed_values ?? [];
                    const fixedValuesFormArray = this.form.get('fixedValues') as UntypedFormArray;
                    fixedValuesFormArray.clear();
                    channel.currencies.forEach(currency => {
                        const fixedValueCtrl = this.#fb.group({
                            amount: [fixedValues?.find(value => value.currency_code === currency.code)?.amount ?? 0,
                            [Validators.required, greaterThanValidator(0)]],
                            currency_code: currency.code
                        });
                        fixedValuesFormArray.push(fixedValueCtrl);
                    });
                    if (fixedValues.length === 1) { // copy value multicurrency in monocurrency form control
                        this.form.get('fixValue').setValue(fixedValues[0].amount);
                    }
                    if (originalData.percentage_value) this.form.get('percentualValue').setValue(originalData.percentage_value);
                }

                if (promotion.discount?.type) {
                    this.form.markAllAsTouched();
                } else {
                    this.form.markAsUntouched();
                }
                this.form.markAsPristine();
            });
    }

    cancel(): void {
        this.#channelPromotionSrv.loadPromotion(this.#channelId, this.#promotionId);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        // TODO MULTICURRENCY: Delete if
        if (this.#isMultiCurrency && this.#hasMoreThanOneCurrencyBS.value) this.form.get('fixValue').disable({ emitEvent: false });
        if (this.form.valid) {
            const data = this.form.value;
            const promotion: ChannelPromotion = {
                discount: {
                    type: data.type,
                    value: data.type === ChannelPromotionDiscountType.percentage ? data.percentualValue : data.fixValue
                }
            };
            if (this.#isMultiCurrency) {
                this.currencies$.subscribe(currencies => {
                    if (this.#hasMoreThanOneCurrencyBS.value) {
                        promotion.discount.fixed_values = data.fixedValues ? data.fixedValues : null;
                        promotion.discount.percentage_value = data.percentualValue ? data.percentualValue : null;
                    } else {
                        data.type === ChannelPromotionDiscountType.fixed ?
                            promotion.discount.fixed_values = [{ amount: data.fixValue, currency_code: currencies[0] }] : null;
                        data.type === ChannelPromotionDiscountType.percentage ?
                            promotion.discount.percentage_value = data.percentualValue : null;
                        promotion.discount.value = null;
                    }
                });
            }

            let canContinue$: Observable<boolean>;
            if (data.type === ChannelPromotionDiscountType.dynamic) {
                canContinue$ = this.#messageDialogSrv.showWarn({
                    title: 'CHANNELS.PROMOTIONS.DISCOUNT_TYPE_OPTS.DYNAMIC_WARN_TITLE',
                    message: 'CHANNELS.PROMOTIONS.DISCOUNT_TYPE_OPTS.DYNAMIC_WARN_MESSAGE',
                    showCancelButton: true,
                    size: DialogSize.SMALL,
                    actionLabel: 'FORMS.ACTIONS.SAVE'
                });
            } else {
                canContinue$ = of(true);
            }

            return canContinue$
                .pipe(
                    switchMap(canContinue => {
                        if (!canContinue) return of(null);

                        return this.#channelPromotionSrv.updatePromotion(this.#channelId, this.#promotionId, promotion)
                            .pipe(
                                switchMap(() => {
                                    this.#ephemeralMessageSrv.showSaveSuccess();
                                    this.#channelPromotionSrv.loadPromotion(this.#channelId, this.#promotionId);
                                    return this.#channelPromotionSrv.getPromotion$()
                                        .pipe(skip(1), first());
                                })
                            );
                    })
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return throwError(() => new Error('Invalid form'));
        }
    }
}
