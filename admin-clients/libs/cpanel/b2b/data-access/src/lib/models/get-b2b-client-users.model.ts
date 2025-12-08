import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { B2bClientUser } from './b2b-client.model';

export interface GetB2bClientUsersRequest extends PageableFilter {
    entity_id?: number;
}

export interface GetB2bClientUsersResponse extends ListResponse<B2bClientUser> {
}
