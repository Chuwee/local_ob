import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { B2bClientReduced } from './b2b-client.model';

export interface GetB2bClientsRequest extends PageableFilter {
    entity_id?: number;
}

export interface GetB2bClientsResponse extends ListResponse<B2bClientReduced> {
}
