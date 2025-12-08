import { PaymentType, VoucherOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
    selector: 'app-voucher-order-details-payments',
    templateUrl: './voucher-order-details-payments.component.html',
    styleUrls: ['./voucher-order-details-payments.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderDetailsPaymentsComponent {
    paymentsColumns = ['date', 'method', 'description', 'price'];
    orderDetail: VoucherOrderDetail;
    orderDate: string;
    paymentsTotalAmount: number;
    dateTimeFormats = DateTimeFormats;
    paymentType = PaymentType;

    @Input() set orderData(orderData: VoucherOrderDetail) {
        this.orderDetail = orderData;
        this.orderDate = orderData.date;
        this.paymentsTotalAmount = orderData.payment_data.map(payment => payment.value)
            .reduce((total, amount) => total + amount, 0);
    }

    @Input() currency: string;
}
