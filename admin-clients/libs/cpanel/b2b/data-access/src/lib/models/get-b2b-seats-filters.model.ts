import { ListResponse } from '@OneboxTM/utils-state';
import { IdName, PageableFilter } from '@admin-clients/shared/data-access/models';

export enum B2bSeatsFilter {
    events = 'events',
    sessions = 'sessions'
}
export interface GetB2bSeatsFiltersRequest extends PageableFilter {
    entity_ids?: number[];
    event_ids?: number[];
    from?: string;
    to?: string;
    term?: string;
}

export interface GetB2bSeatsFiltersResponse extends ListResponse<IdName> {
}
