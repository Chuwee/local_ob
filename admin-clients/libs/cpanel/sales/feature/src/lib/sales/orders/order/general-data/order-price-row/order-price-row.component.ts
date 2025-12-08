import { AbsoluteAmountPipe, AmountPrefixPipe, CurrencyPrefixType, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
    selector: 'app-order-price-row',
    template: `
        <div class="order-summary-price-row flex flex-wrap flex-row gap-2">
            <div class="flex-1">
                {{$label()}}
            </div>
            <div class="flex-none">
                @if($prefix()) {
                    {{$value() | absoluteAmount | localCurrency: $ccy() | amountPrefix: $value() : $prefix()}}
                } @else {
                    {{$value() | localCurrency: $ccy()}}
                }
            </div>
        </div>
    `,
    imports: [AbsoluteAmountPipe, LocalCurrencyPipe, AmountPrefixPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderPriceRowComponent {
    readonly $value = input<number>(0, { alias: 'value' });
    readonly $ccy = input<string>(null, { alias: 'ccy' });
    readonly $label = input<string>(null, { alias: 'label' });
    readonly $prefix = input<CurrencyPrefixType>(null, { alias: 'prefix' });
}
