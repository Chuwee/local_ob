import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { B2bCondition, B2bConditionsClientsGroupType } from './b2b-condition.model';
import { B2bConditionsClient } from './b2b-conditions-client.model';
import { EventOrEntityId } from './b2b-conditions.model';
import { ListResponse } from '@OneboxTM/utils-state';

export type GetB2bConditionsClientsRequest<T extends B2bConditionsClientsGroupType> = PageableFilter & EventOrEntityId<T>;

export interface GetB2bConditionsClientsResponse extends ListResponse<B2bConditionsClient> {
}

export interface PutB2bConditionsClients {
    id: number; // operatorId | entityId | eventId
    clients: { id: number; conditions: B2bCondition[] }[];
}

export type DeleteB2bConditionsClientsRequest<T extends B2bConditionsClientsGroupType> = EventOrEntityId<T> & {
    clients_ids?: number[];
};
