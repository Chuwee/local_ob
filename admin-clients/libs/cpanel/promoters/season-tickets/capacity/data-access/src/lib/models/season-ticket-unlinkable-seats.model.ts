// Only used for typing the http request
export interface SeasonTicketUnLinkableSeats {
    results: SeasonTicketUnLinkableSeat[];
}

export interface SeasonTicketUnLinkableSeat {
    id: number;
    result: boolean;
    reason?: SeasonTicketNotUnLinkableSeatReason;
}
// Not used at the moment because all the cases that could give this reasons as a response from the backend are
// already contemplated as a constrain in the frontend, so the request cannot be performed in first place
enum SeasonTicketNotUnLinkableSeatReason {
    notLinked = 'SEAT_ALREADY_NOT_LINKED',
    sold = 'SEAT_SOLD_IN_SEASON_TICKET'
}

