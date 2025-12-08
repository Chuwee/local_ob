import { SeatNotLinkableReason } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';

export interface SeasonTicketLinkableSeats {
    results: SeasonTicketLinkableSeat[];
}

export interface SeasonTicketLinkableSeat {
    id: number;
    result: boolean;
    reason?: SeatNotLinkableReason;
}
