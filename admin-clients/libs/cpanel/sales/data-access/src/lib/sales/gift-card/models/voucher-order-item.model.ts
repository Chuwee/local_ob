import { VoucherOrderEmailStatus } from './voucher-orders-email-status.enum';

export interface VoucherOrderDetailItem {
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
    delivery_date: string;
    delivery_status: VoucherOrderEmailStatus;
    receiver: {
        name: string;
        last_name: string;
        email: string;
        scheduled_date: string;
        message: string;
    };
}
