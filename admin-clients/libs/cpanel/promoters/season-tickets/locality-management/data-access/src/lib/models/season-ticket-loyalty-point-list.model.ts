export interface SeasonTicketLoyaltyPointList {
    sessions?: SeasonTicketLoyaltyPointSession[];
}

export interface PutSeasonTicketLoyaltyPoint {
    sessions?: SeasonTicketLoyaltyPointSession[];
}

export interface SeasonTicketLoyaltyPointSession {
    sessionId: number;
    transfer?: number;
    attendance?: number;
}