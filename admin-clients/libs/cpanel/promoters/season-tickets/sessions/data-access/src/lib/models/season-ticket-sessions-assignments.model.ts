import { SeasonTicketSessionsResult, SeasonTicketSessionWithReason } from './season-ticket-sessions-reasons.model';

export type SeasonTicketSessionsAssignments = SeasonTicketSessionsResult<SeasonTicketSessionAssignment>;

export interface SeasonTicketSessionAssignment extends SeasonTicketSessionWithReason {
    session_assigned: boolean;
}
