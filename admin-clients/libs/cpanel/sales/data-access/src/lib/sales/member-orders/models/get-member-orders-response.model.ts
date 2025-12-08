import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { MemberOrder } from './member-order.model';

export interface GetMemberOrdersResponse extends ListResponse<MemberOrder> {
    aggregated_data: ResponseAggregatedData;
}
