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
    ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit, ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, of, switchMap } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';
import { ChannelOperativeService } from '../../channel-operative.service';
import {
    ChannelSurchargesGenericRangesComponent
} from './ranges/channel-surcharges-generic-ranges.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, RangeTableComponent, TabsMenuComponent, TabDirective, ChannelSurchargesGenericRangesComponent,
        LocalCurrencyPartialTranslationPipe, RangeCurrencyInputPipe, AsyncPipe
    ],
    selector: 'app-channel-surcharges-generic',
    templateUrl: './channel-surcharges-generic.component.html'
})
export class ChannelSurchargesGenericComponent implements OnInit {
    readonly #formGroup = inject(FormGroupDirective);
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);

    @ViewChild(TabsMenuComponent) private readonly _tabsMenuComponent: TabsMenuComponent;

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$().pipe(first());
    readonly channelCurrencies$ = inject(ChannelsService).getChannel$().pipe(first(), map(channel => channel.currencies));
    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly genericForm = this.#fb.nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly errorCtrl = this.#fb.nonNullable.control('');
    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly data$ = this.#channelOperativeSrv.getChannelSurcharges$()
        .pipe(
            filter(Boolean),
            map(surcharges =>
                surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.generic)
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
                            const genericRanges = surcharges
                                .find(surcharge => surcharge.type === ChannelSurchargeType.generic)
                                ?.ranges ?? [];
                            const rangesMap = new Map<string, RangeElement[]>();
                            genericRanges.forEach(range => {
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

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    #noMultiCurrency(): void {
        this.#formGroup.control.addControl('generic', this.genericForm);
        this.$surchargesRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(surchargesRequest => {
                if (this.#formGroup.control.invalid) return;

                const surcharge: ChannelSurcharge = {
                    type: ChannelSurchargeType.generic,
                    ranges: cleanRangesBeforeSave(this.genericForm.value.ranges)
                };
                surchargesRequest.push(surcharge);
            });
    }
}
