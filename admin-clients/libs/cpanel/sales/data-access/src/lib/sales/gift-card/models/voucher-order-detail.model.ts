import { PaymentDetail } from '../../orders/models/payment-detail.model';
import { PaymentType } from '../../orders/models/payment-type.enum';
import { VoucherOrderDetailItem } from './voucher-order-item.model';
import { VoucherOrderType } from './voucher-order-type.enum';

export interface VoucherOrderDetail {
    code: string;
    type: VoucherOrderType;
    language: string;
    date: string;
    channel: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        };
    };
    buyer_data: {
        name: string;
        last_name: string;
        email: string;
    };
    price: {
        final: number;
        currency: string;
    };
    payment_data: {
        reference: string;
        type: PaymentType;
        value: number;
    }[];
    payment_detail: PaymentDetail;
    items: VoucherOrderDetailItem[];
}

