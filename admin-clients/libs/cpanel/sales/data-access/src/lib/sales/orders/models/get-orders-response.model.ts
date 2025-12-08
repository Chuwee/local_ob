import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { Order } from './order.model';

export interface GetOrdersResponse extends ListResponse<Order> {
    aggregated_data: ResponseAggregatedData;
}
