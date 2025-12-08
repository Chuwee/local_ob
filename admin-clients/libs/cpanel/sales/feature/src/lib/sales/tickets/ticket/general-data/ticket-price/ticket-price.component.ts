import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { AbsoluteAmountPipe, AmountPrefixPipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { OrderTaxesInfoComponent } from '../../../../orders/order/general-data/order-taxes-info/order-taxes-info.component';

@Component({
    selector: 'app-ticket-price',
    templateUrl: './ticket-price.component.html',
    styleUrls: ['./ticket-price.component.css'],
    imports: [MatDivider, TranslatePipe, LocalCurrencyPipe, AbsoluteAmountPipe, AmountPrefixPipe, OrderTaxesInfoComponent],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketPriceComponent {
    readonly $ticket = input.required<TicketDetail>({ alias: 'ticket' });

    readonly $hasTaxes = computed(() => Boolean(this.$ticket()?.price?.taxes));

    readonly $totalTaxes = computed(() => {
        if (!this.$ticket() || !this.$hasTaxes()) return 0;
        return (this.$ticket().price.taxes.item?.total || 0) + (this.$ticket().price.taxes.charges?.total || 0);
    });

    readonly $itemsTaxes = computed(() => {
        if (!this.$ticket() || !this.$hasTaxes()) return { items: 0, charges: 0 };
        return {
            items: Math.abs(this.$ticket().price.taxes.item?.total || 0),
            charges: Math.abs(this.$ticket().price.taxes.charges?.total || 0)
        };
    });
}
