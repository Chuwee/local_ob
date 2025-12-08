export enum SeasonTicketAssignableSessionReason {
    sessionAssignableInvalidStatus = 'SESSION_ASSIGNABLE_INVALID_STATUS',
    sessionAssignableRestricted = 'SESSION_ASSIGNABLE_RESTRICTED'
}

export interface SeasonTicketAssignableSession {
    assignable: boolean;
    reason: SeasonTicketAssignableSessionReason;
}
