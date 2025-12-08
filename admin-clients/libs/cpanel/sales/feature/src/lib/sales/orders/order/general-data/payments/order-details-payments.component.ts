import { OrderDetail, PaymentData, PaymentType, VoucherType } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-order-details-payments',
    templateUrl: './order-details-payments.component.html',
    styleUrls: ['./order-details-payments.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OrderDetailsPaymentsComponent {

    private readonly _router = inject(Router);

    readonly paymentsColumns = ['date', 'method', 'description', 'price'];
    readonly paymentType = PaymentType;
    readonly dateTimeFormats = DateTimeFormats;
    readonly voucherType = VoucherType;

    payments: OrderDetail['payment_data'];
    paymentsTotalAmount: number;
    pgpPayments: { [key: string]: string }[];
    pgpSaleTransactionId: string;
    paymentDetailMethod: OrderDetail['payment_detail']['payment_method'];
    voucherInfo: OrderDetail['payment_detail']['voucher'];
    currency: string;

    @Input() set order(order: OrderDetail) {
        this.payments = order.payment_data;
        this.paymentsTotalAmount = order.payment_data.reduce((total, payment) => total + payment.value, 0);
        this.currency = order.price.currency;
        const payment = order.payment_detail;
        this.voucherInfo = payment?.voucher;
        this.paymentDetailMethod = payment?.payment_method;
        this.pgpPayments = payment?.gateway_additional_info?.['pgp_payments'];
        this.pgpSaleTransactionId = payment?.gateway_additional_info?.['pgp_sale_transaction_id']?.toString();
    }

    goToVoucher(payment: PaymentData): void {
        if (payment.type === PaymentType.voucher
            && this.voucherInfo?.group?.id
            && this.voucherInfo?.group?.type !== this.voucherType.seasonTicket
        ) {
            this._router.navigate(['/vouchers', this.voucherInfo.group.id, 'voucher-codes', this.voucherInfo.code]);
        }
    }

}
