import { SeasonTicketRenewalAvailableSeat } from './season-ticket-renewal-available-seat.model';

export interface VmSeasonTicketRenewalAvailableSeat extends SeasonTicketRenewalAvailableSeat {
    assignedTo?: string;
    alreadyMappedTo?: string;
}
