import { VoucherEmailFormat } from './voucher-email-type.enum';

export interface ResendVoucherRequest {
    type: VoucherEmailFormat;
    email?: string;
    subject?: string;
    body?: string;
    language: string;
}
