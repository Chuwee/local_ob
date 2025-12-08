import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { Sale } from './sales.model';

export interface GetSalesResponse extends ListResponse<Sale> {
    aggregated_data: ResponseAggregatedData;
}
