import { TypeOrderExpire } from '@admin-clients/cpanel/promoters/events/data-access';
import { SeasonTicketRelativeTimeUnits } from './season-ticket-relative-time-units.enum';

export interface SeasonTicketExpirationOrder {
    timespan?: SeasonTicketRelativeTimeUnits;
    timespan_amount?: number;
    expiration_time?: number;
    expiration_type?: TypeOrderExpire;

}
