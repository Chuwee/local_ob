import { SeasonTicketSessionsResult, SeasonTicketSessionWithReason } from './season-ticket-sessions-reasons.model';

export type SeasonTicketSessionsValidations = SeasonTicketSessionsResult<SeasonTicketSessionValidation>;

export interface SeasonTicketSessionValidation extends SeasonTicketSessionWithReason {
    session_valid: boolean;
}
