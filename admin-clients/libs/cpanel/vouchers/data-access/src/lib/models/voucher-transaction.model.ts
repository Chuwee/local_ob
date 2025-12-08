import { VoucherTransactionType } from './voucher-transaction-type.enum';

export interface Transaction {
    date: string;
    amount: number;
    balance: number;
    type: VoucherTransactionType;
    code: string;
}
