import { ListResponse } from '@OneboxTM/utils-state';
import { SeasonTicketSession } from './season-ticket-session.model';
import { SeasonTicketSessionsSummary } from './season-ticket-sessions-summary.model';

export interface GetSeasonTicketSessionsResponse extends ListResponse<SeasonTicketSession> {
    summary: SeasonTicketSessionsSummary;
}
