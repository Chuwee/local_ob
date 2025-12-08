import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { Buyer } from './buyer.model';

export interface GetBuyerResponse extends ListResponse<Buyer> {
    aggregated_data: ResponseAggregatedData;
}
