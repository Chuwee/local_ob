export interface GetSeasonTicketStatusResponse {
    season_ticket_id: number;
    generation_status: SeasonTicketGenerationStatus;
    status?: SeasonTicketStatus;
}

export enum SeasonTicketGenerationStatus {
    inProgress = 'IN_PROGRESS',
    ready = 'READY',
    error = 'ERROR'
}

export enum SeasonTicketStatus {
    setUp = 'SET_UP',
    pendingPublication = 'PENDING_PUBLICATION',
    ready = 'READY',
    cancelled = 'CANCELLED',
    finished = 'FINISHED'
}
