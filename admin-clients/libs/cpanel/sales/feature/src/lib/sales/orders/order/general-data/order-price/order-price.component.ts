import { OrderDetail, OrdersService, PaymentType, VoucherType } from '@admin-clients/cpanel-sales-data-access';
import { OrderItemType, OrderType } from '@admin-clients/shared/common/data-access';
import { AbsoluteAmountPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { OrderPriceRowComponent } from '../order-price-row/order-price-row.component';
import { OrderTaxesInfoComponent } from '../order-taxes-info/order-taxes-info.component';

@Component({
    selector: 'app-order-price',
    templateUrl: './order-price.component.html',
    styleUrls: ['./order-price.component.scss'],
    imports: [TranslatePipe, MatDivider, OrderPriceRowComponent, OrderTaxesInfoComponent, AbsoluteAmountPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderPriceComponent {
    readonly #ordersService = inject(OrdersService);
    readonly orderTypes = OrderType;
    readonly voucherType = VoucherType;

    readonly $order = input<OrderDetail>(null, { alias: 'order' });

    readonly $products = computed(() => {
        const order = this.$order();
        return order?.items?.filter(item => item.type === OrderItemType.product);
    });

    readonly $tickets = computed(() => {
        const order = this.$order();
        return order?.items?.filter(item => item.type !== OrderItemType.product);
    });

    readonly $ticketsBasePrice = computed(() =>
        this.$tickets()?.reduce((acc, item) => acc + item.price.base, 0) ?? 0
    );

    readonly $productsBasePrice = computed(() =>
        this.$products()?.reduce((acc, item) => acc + item.price.base, 0) ?? 0
    );

    readonly $voucherPrice = computed(() => {
        const order = this.$order();
        return order?.payment_data?.find(payment => payment.type === PaymentType.voucher);
    });

    readonly $itemsTaxesTotal = computed(() => {
        const order = this.$order();
        const tickets = this.$tickets();
        if (!order || !tickets) return { items: 0, charges: 0 };
        return this.#ordersService.calculateTaxesForItems(tickets, order);
    });

    readonly $productsTaxesTotal = computed(() => {
        const order = this.$order();
        const products = this.$products();
        if (!order || !products) return { items: 0, charges: 0 };
        return this.#ordersService.calculateTaxesForItems(products, order);
    });

    readonly $isTaxes = computed(() => Boolean(this.$order()?.price?.taxes));

    readonly $totalTaxes = computed(() => {
        const surchargesTaxes = Math.max(0, this.$itemsTaxesTotal().charges) + Math.max(0, this.$productsTaxesTotal().charges);
        const itemsTaxes = Math.max(0, this.$itemsTaxesTotal().items) + Math.max(0, this.$productsTaxesTotal().items);
        return surchargesTaxes + itemsTaxes;
    });
}
