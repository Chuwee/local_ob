import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { VoucherOrder } from './voucher-order.model';

export interface GetVoucherOrdersResponse extends ListResponse<VoucherOrder> {
    aggregated_data: ResponseAggregatedData;
}
