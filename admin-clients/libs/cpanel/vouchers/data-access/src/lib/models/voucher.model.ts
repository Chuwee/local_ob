import { VoucherLimitlessValue } from './voucher-limitlessValue.enum';
import { VoucherStatus } from './voucher-status.enum';
import { Transaction } from './voucher-transaction.model';

export interface Voucher {
    code: string;
    voucher_group: {
        id: number;
        name: string;
    };
    status: VoucherStatus;
    pin?: string;
    email?: string;
    balance?: number;
    usage?: {
        used?: number;
        limit: {
            type: VoucherLimitlessValue;
            value: number;
        };
    };
    expiration?: {
        enable: boolean;
        date: string;
    };
    transactions?: Transaction[];
}
