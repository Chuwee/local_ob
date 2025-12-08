import { SessionsFilterFields } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { SeasonTicketSessionStatus } from './season-ticket-session-status.enum';

export interface GetSeasonTicketSessionsRequest extends PageableFilter {
    status?: SeasonTicketSessionStatus;
    startDate?: string;
    endDate?: string;
    event_id?: string;
    fields?: SessionsFilterFields[];
}
