import { MemberOrderDetail, PaymentType } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
    selector: 'app-member-order-payments',
    templateUrl: './member-order-payments.component.html',
    styleUrls: ['./member-order-payments.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrderPaymentsComponent {
    paymentsColumns = ['date', 'method', 'description', 'price'];
    payments: MemberOrderDetail['payment_data'];
    paymentsTotalAmount: number;
    paymentType = PaymentType;
    dateTimeFormats = DateTimeFormats;
    currency: string;

    @Input() set order(order: MemberOrderDetail) {
        this.payments = order?.payment_data;
        this.paymentsTotalAmount = this.payments?.reduce((total, payment) => total + payment.value, 0);
        this.currency = order.price.currency;
    }

    constructor() { }

}
