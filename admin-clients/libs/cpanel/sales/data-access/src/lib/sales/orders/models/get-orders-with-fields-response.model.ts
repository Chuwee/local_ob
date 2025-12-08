import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { OrderWithFields } from './order-with-fields.model';
import { ListResponse } from '@OneboxTM/utils-state';

export interface GetOrdersWithFieldsResponse extends ListResponse<OrderWithFields> {
    aggregated_data: ResponseAggregatedData;
}
