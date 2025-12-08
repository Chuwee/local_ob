export enum SeasonTicketUnAssignSessionReason {
    atLeastOneSeatIsSoldOrNotAvailable = 'AT_LEAST_ONE_SEAT_IS_SOLD_OR_NOT_AVAILABLE',
    notValidUnAssignReason = 'NOT_VALID_UNASSIGN_REASON',
    secMktLocationsFound = 'SEC_MKT_LOCATIONS_FOUND'
}

export interface SeasonTicketSessionUnAssignment {
    session_unassigned: boolean;
    season_ticket_id: number;
    session_id: number;
    reason?: SeasonTicketUnAssignSessionReason;
}
