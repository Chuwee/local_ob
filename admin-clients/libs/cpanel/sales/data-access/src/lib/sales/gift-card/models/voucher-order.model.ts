import { VoucherOrderType } from './voucher-order-type.enum';
import { VoucherOrderEmailStatus } from './voucher-orders-email-status.enum';

export interface VoucherOrder {
    code: string;
    type: VoucherOrderType;
    language: string;
    date: string;
    items: {
        group: {
            id: number;
            name: string;
        };
        code: string;
        price: {
            balance: number;
            final: number;
            currency: string;
        };
        expiration_date: string;
        deilvery_date: string;
        delivery_status: VoucherOrderEmailStatus;
        receiver: {
            name: string;
            last_name: string;
            email: string;
        };
    }[];
    channel: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        };
    };
    price: {
        final: number;
        currency: string;
    };
    buyer_data: {
        name: string;
        last_name: string;
        email: string;
    };
    payment_data: {
        reference: string;
    }[];
}
