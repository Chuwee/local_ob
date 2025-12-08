export interface SeasonTicketLinkNNZResponse {
    linked_seats: number;
    not_linked_seats: number;
}

export interface SeasonTicketUnlinkNNZResponse {
    unlinked_seats: number;
}

export enum SeasonTicketNotLinkableNNZReason {
    someSeatsAreNotFree = 'SOME_SEATS_ARE_NOT_FREE'
}
