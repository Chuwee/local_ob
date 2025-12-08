import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { VoucherGroupStatus } from './voucher-group-status.enum';
import { VoucherGroupType } from './voucher-group-type.enum';

export interface GetVoucherGroupsRequest extends PageableFilter {
    entity_id?: number;
    status?: VoucherGroupStatus;
    type?: VoucherGroupType;
    currency_code?: string;
}
