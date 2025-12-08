import {
    ChannelsService, ChannelSurcharge, ChannelSurchargeType
} from '@admin-clients/cpanel/channels/data-access';
import { isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import {
    cleanRangesBeforeSave, RangeCurrencyInputPipe, RangeTableComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { LocalCurrencyPartialTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit, signal, ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, of, switchMap } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';
import { ChannelOperativeService } from '../../channel-operative.service';
import {
    ChannelSurchargesPromotionRangesComponent
} from './ranges/channel-surcharges-promotion-ranges.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, RangeTableComponent, TabsMenuComponent, TabDirective, ChannelSurchargesPromotionRangesComponent,
        LocalCurrencyPartialTranslationPipe, RangeCurrencyInputPipe, AsyncPipe
    ],
    selector: 'app-channel-surcharges-promotion',
    templateUrl: './channel-surcharges-promotion.component.html'
})
export class ChannelSurchargesPromotionComponent implements OnInit {
    readonly #formGroup = inject(FormGroupDirective);
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);

    @ViewChild(TabsMenuComponent) private readonly _tabsMenuComponent: TabsMenuComponent;

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$().pipe(first());
    readonly channelCurrencies$ = inject(ChannelsService).getChannel$().pipe(first(), map(channel => channel.currencies));
    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly promotionForm = this.#fb.nonNullable.group({
        enabledRanges: false,
        promotionRanges: this.#fb.nonNullable.group({
            ranges: [{ value: null as RangeElement[], disabled: true }]
        })
    });

    readonly errorCtrl = this.#fb.nonNullable.control('');
    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly data$ = this.#channelOperativeSrv.getChannelSurcharges$()
        .pipe(
            filter(Boolean),
            map(surcharges =>
                surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.promotion)
                    ?.ranges ?? []
            )
        );

    readonly multiCurrencyData$ = isMultiCurrency$()
        .pipe(
            first(),
            switchMap(isMultiCurrency => {
                if (!isMultiCurrency) return of(new Map());

                return this.#channelOperativeSrv.getChannelSurcharges$()
                    .pipe(
                        filter(Boolean),
                        map(surcharges => {
                            const promotionRanges = surcharges
                                .find(surcharge => surcharge.type === ChannelSurchargeType.promotion)
                                ?.ranges ?? [];
                            const rangesMap = new Map<string, RangeElement[]>();
                            promotionRanges.forEach(range => {
                                const ranges = rangesMap.get(range.currency_code);
                                if (ranges) {
                                    ranges.push(range);
                                } else {
                                    rangesMap.set(range.currency_code, [range]);
                                }
                            });
                            return rangesMap;
                        })
                    );
            })
        );

    readonly $hidePromotions = signal(true);

    readonly $enabledPromotionRangesCtrl = input.required<FormControl<boolean>>({ alias: 'enabledPromotionRangesCtrl' });
    readonly $userCanWrite = input.required<boolean>({ alias: 'userCanWrite' });
    readonly $currencySelectedTabBS = input.required<BehaviorSubject<string>>({ alias: 'currencySelectedTabBS' });
    readonly $surchargesRequestCtrl = input.required<FormControl<ChannelSurcharge[]>>({ alias: 'surchargesRequestCtrl' });

    ngOnInit(): void {
        // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
        this.isMultiCurrency$
            .pipe(first())
            .subscribe(isMultiCurrency => {
                if (!isMultiCurrency) {
                    this.#noMultiCurrency();
                } else {
                    this.#formGroup.control.addControl('promotionEnabledRanges', this.$enabledPromotionRangesCtrl());

                    this.$enabledPromotionRangesCtrl().valueChanges
                        .pipe(takeUntilDestroyed(this.#destroyRef))
                        .subscribe(value => this.$hidePromotions.set(!value));

                    this.$surchargesRequestCtrl().valueChanges
                        .pipe(takeUntilDestroyed(this.#destroyRef))
                        .subscribe(() => {
                            this.errorCtrl.reset('', { emitEvent: false });
                        });

                    this.errorCtrl.valueChanges
                        .pipe(takeUntilDestroyed(this.#destroyRef))
                        .subscribe(error => {
                            if (error) this._tabsMenuComponent.goToKeyTab(error);
                        });

                    this.$currencySelectedTabBS()
                        .pipe(takeUntilDestroyed(this.#destroyRef))
                        .subscribe(currency => {
                            if (!currency) return;
                            this._tabsMenuComponent.goToKeyTab(currency);
                        });
                }
            });
    }

    #noMultiCurrency(): void {
        this.#formGroup.control.addControl('promotion', this.promotionForm);
        this.#channelOperativeSrv.getChannelSurcharges$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(surcharges => {
                const promotionSurcharges = surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.promotion);
                if (promotionSurcharges) {
                    this.$enabledPromotionRangesCtrl().setValue(promotionSurcharges.enabled_ranges, { emitEvent: false });
                }
                this.$hidePromotions.set(!promotionSurcharges?.enabled_ranges);
            });

        this.$surchargesRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(surchargesRequest => {
                if (this.#formGroup.control.invalid) return;
                const surcharge: ChannelSurcharge = {
                    type: ChannelSurchargeType.promotion,
                    ranges: cleanRangesBeforeSave(this.promotionForm.value.promotionRanges.ranges),
                    enabled_ranges: this.$enabledPromotionRangesCtrl().value
                };
                surchargesRequest.push(surcharge);
                this.$surchargesRequestCtrl().setValue(surchargesRequest, { emitEvent: false });
            });
    }
}
