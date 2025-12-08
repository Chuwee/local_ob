export enum SeasonTicketValidateOrAssignSessionReason {
    eventOrSessionStatusNotValid = 'EVENT_OR_SESSION_STATUS_NOT_VALID',
    originSessionNotLinkableSeats = 'ORIGIN_SESSION_NOT_LINKABLE_SEATS',
    oneSectorMinNotFoundInTarget = 'ONE_SECTOR_MIN_NOT_FOUND_IN_TARGET',
    oneLinkableSeatNotFoundInTarget = 'ONE_LINKABLE_SEAT_NOT_FOUND_IN_TARGET',
    atLeastOneSeatIsNotFoundInTargetSession = 'AT_LEAST_ONE_SEAT_IS_NOT_FOUND_IN_TARGET_SESSION',
    sessionVenueNotGraphic = 'SESSION_VENUE_NOT_GRAPHIC',
    atLeastOneSeatIsSoldOrNotAvailable = 'AT_LEAST_ONE_SEAT_IS_SOLD_OR_NOT_AVAILABLE',
    notValidReason = 'NOT_VALID_REASON'
}

export interface SeasonTicketSessionWithReason {
    season_ticket_id: number;
    session_id: number;
    reason?: SeasonTicketValidateOrAssignSessionReason;
}

export interface SeasonTicketSessionsResult<T> {
    result: T[];
}

