import { VoucherLimitlessValue } from './voucher-limitlessValue.enum';

export interface PostVoucher {
    balance: number;
    expiration: string;
    pin?: string;
    email?: string;
    usage_limit?: {
        type: VoucherLimitlessValue;
        value?: number;
    };
}
