import { VoucherLimitlessValue } from './voucher-limitlessValue.enum';
import { VoucherStatus } from './voucher-status.enum';

export interface PutVoucher {
    status?: VoucherStatus;
    pin?: string;
    email?: string;
    usage?: {
        limit: {
            type: VoucherLimitlessValue;
            value: number;
        };
    };
    expiration?: {
        enable: boolean;
        date: string;
    };
    balance?: number;
}
