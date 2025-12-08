import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { AbsoluteAmountPipe, AmountPrefixPipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { OrderTaxesInfoComponent } from '../../../../orders/order/general-data/order-taxes-info/order-taxes-info.component';

@Component({
    selector: 'app-product-sales-price',
    templateUrl: './product-sales-price.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        LocalCurrencyPipe,
        AmountPrefixPipe,
        AbsoluteAmountPipe,
        MatDividerModule,
        OrderTaxesInfoComponent
    ]
})
export class ProductSalesPriceComponent {
    readonly $ticketDetail = input<TicketDetail>(null, { alias: 'ticketDetail' });

    readonly $hasTaxes = computed(() => Boolean(this.$ticketDetail()?.price?.taxes));

    readonly $totalTaxes = computed(() => {
        if (!this.$ticketDetail() || !this.$hasTaxes()) return 0;
        return (this.$ticketDetail().price.taxes.item?.total || 0) + (this.$ticketDetail().price.taxes.charges?.total || 0);
    });

    readonly $productsTaxes = computed(() => {
        if (!this.$ticketDetail() || !this.$hasTaxes()) return { items: 0, charges: 0 };
        return {
            items: Math.abs(this.$ticketDetail().price.taxes.item?.total || 0),
            charges: Math.abs(this.$ticketDetail().price.taxes.charges?.total || 0)
        };
    });
}
