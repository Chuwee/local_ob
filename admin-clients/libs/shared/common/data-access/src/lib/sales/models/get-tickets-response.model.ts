import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { OrderItemDetails } from './order-item-details.model';

export interface GetTicketsResponse extends ListResponse<OrderItemDetails> {
    aggregated_data: ResponseAggregatedData;
}
