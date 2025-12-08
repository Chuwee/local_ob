import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { B2bSeatReduced, B2bSeatType } from './b2b-seat.model';
import { ListResponse } from '@OneboxTM/utils-state';

export interface GetB2bSeatsListRequest extends PageableFilter {
    fields?: string[];
    entity_ids?: number[];
    channel_ids?: number[];
    client_entity_ids?: number[];
    client_ids?: number[];
    event_ids?: number[];
    session_ids?: number[];
    types?: B2bSeatType[];
    date_from?: string;
    date_to?: string;
}

export interface GetB2bSeatsListResponse extends ListResponse<B2bSeatReduced> {
}
