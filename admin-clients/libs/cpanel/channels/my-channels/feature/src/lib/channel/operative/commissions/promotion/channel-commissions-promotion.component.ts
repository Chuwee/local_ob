import {
    ChannelCommission, ChannelCommissionType, ChannelsService
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
import { FormBuilder, FormControl, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, of, switchMap } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';
import { ChannelOperativeService } from '../../channel-operative.service';
import { ChannelCommissionsPromotionRangesComponent } from './ranges/channel-commissions-promotion-ranges.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, ReactiveFormsModule, RangeTableComponent, TranslatePipe, TabsMenuComponent, TabDirective,
        ChannelCommissionsPromotionRangesComponent, LocalCurrencyPartialTranslationPipe, RangeCurrencyInputPipe
    ],
    selector: 'app-channel-commissions-promotion',
    templateUrl: './channel-commissions-promotion.component.html'
})
export class ChannelCommissionsPromotionComponent implements OnInit {
    readonly #formGroup = inject(FormGroupDirective);
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);

    @ViewChild(TabsMenuComponent) private readonly _tabsMenuComponent: TabsMenuComponent;

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$().pipe(first());
    readonly channelCurrencies$ = inject(ChannelsService).getChannel$().pipe(
        first(),
        map(channel => channel.currencies)
    );

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly promotionForm = this.#fb.nonNullable.group({
        enabledRanges: false,
        promotionRanges: this.#fb.nonNullable.group({
            ranges: [null as RangeElement[]]
        })
    });

    readonly errorCtrl = this.#fb.nonNullable.control('');
    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly data$ = this.#channelOperativeSrv.getChannelCommissions$()
        .pipe(
            filter(Boolean),
            map(commissions =>
                commissions
                    .find(commission => commission.type === ChannelCommissionType.promotion)
                    ?.ranges ?? []
            )
        );

    readonly multiCurrencyData$ = isMultiCurrency$()
        .pipe(
            first(),
            switchMap(isMultiCurrency => {
                if (!isMultiCurrency) return of(new Map());

                return this.#channelOperativeSrv.getChannelCommissions$()
                    .pipe(
                        filter(Boolean),
                        map(commissions => {
                            const promotionRanges = commissions
                                .find(commission => commission.type === ChannelCommissionType.promotion)
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
    readonly $commissionsRequestCtrl = input.required<FormControl<ChannelCommission[]>>({ alias: 'commissionsRequestCtrl' });

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

                    this.$commissionsRequestCtrl().valueChanges
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

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    #noMultiCurrency(): void {
        this.#formGroup.control.addControl('promotion', this.promotionForm);
        this.#channelOperativeSrv.getChannelCommissions$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(commissions => {
                const promotionComissions = commissions
                    .find(comission => comission.type === ChannelCommissionType.promotion);
                if (promotionComissions) {
                    this.$enabledPromotionRangesCtrl().setValue(promotionComissions.enabled_ranges, { emitEvent: false });
                }
                this.$hidePromotions.set(!promotionComissions?.enabled_ranges);
            });

        this.$commissionsRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(comissionsRequest => {
                if (this.#formGroup.control.invalid) return;
                const commission: ChannelCommission = {
                    type: ChannelCommissionType.promotion,
                    ranges: cleanRangesBeforeSave(this.promotionForm.value.promotionRanges.ranges),
                    enabled_ranges: this.$enabledPromotionRangesCtrl().value
                };
                comissionsRequest.push(commission);
                this.$commissionsRequestCtrl().setValue(comissionsRequest, { emitEvent: false });
            });
    }
}
