import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface BiUser {
    id: number;
    entity_id: number;
    name: string;
    last_name: string;
    email: string;
    subscriptions?: BiUserSubscription[];
}

export interface PostBiUser {
    entity_id: number;
    name: string;
    last_name: string;
    email: string;
}

export interface PutBiUser {
    name: string;
    last_name: string;
    email: string;
}
export interface BiUserSubscription {
    userId: number;
    entityId: number;
    eventId: number;
    sessionId?: number;
}

export interface BiUsersRequest extends PageableFilter {
    entity_id: number;
}

export interface BiUsersList extends ListResponse<BiUser> { }
