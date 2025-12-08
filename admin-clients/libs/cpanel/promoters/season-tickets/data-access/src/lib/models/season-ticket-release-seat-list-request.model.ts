import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface SeasonTicketReleaseSeatListRequest extends PageableFilter {
    session_id?: number;
    release_status?: string[];
}
