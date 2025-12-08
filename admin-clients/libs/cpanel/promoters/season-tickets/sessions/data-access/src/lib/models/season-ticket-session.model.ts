import { SeasonTicketSessionStatus } from './season-ticket-session-status.enum';
import { SeasonTicketAssignableSession } from './season-ticket-sessions-assignable.model';

export interface SeasonTicketSession {
    status: SeasonTicketSessionStatus;
    session_id: number;
    session_name: string;
    event_id: number;
    event_name: string;
    session_assignable: SeasonTicketAssignableSession;
    session_starting_date: string;
}

export interface SeasonTicketSessionSelected extends SeasonTicketSession {
    id: number;
    blocked?: boolean;
}
