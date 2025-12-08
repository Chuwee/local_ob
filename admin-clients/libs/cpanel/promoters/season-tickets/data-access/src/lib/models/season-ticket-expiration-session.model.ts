import { SeasonTicketRelativeTimeUnits } from './season-ticket-relative-time-units.enum';

export interface SeasonTicketExpirationSession {
    timespan?: SeasonTicketRelativeTimeUnits;
    timespan_amount?: number;
    expiration_time?: number;
}
