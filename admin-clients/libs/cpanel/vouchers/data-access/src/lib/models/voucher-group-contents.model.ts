import { VoucherGroupContentType } from './voucher-group-content-type.enum';

export interface VoucherContents {
    language: string;
    type: VoucherGroupContentType;
    value: string;
}
