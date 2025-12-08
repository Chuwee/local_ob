import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { Listing } from './listings.model';

export interface GetListingsResponse extends ListResponse<Listing> {
    aggregated_data: ResponseAggregatedData;
}
