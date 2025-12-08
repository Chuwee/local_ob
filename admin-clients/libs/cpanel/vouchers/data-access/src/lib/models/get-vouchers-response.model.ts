import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { Voucher } from './voucher.model';

export interface GetVouchersResponse extends ListResponse<Voucher> {
    aggregated_data: ResponseAggregatedData;
}
