import { ListResponse } from '@OneboxTM/utils-state';
import { Matching } from './matching.model';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';

export interface GetMatchingsResponse extends ListResponse<Matching> {
    aggregated_data: ResponseAggregatedData;
}
