import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { VoucherStatus } from './voucher-status.enum';

export interface GetVoucherRequest extends PageableFilter {
    status?: VoucherStatus;
    aggs?: boolean;
}
