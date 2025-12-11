package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

public enum SeasonTicketInternalGenerationStatus {
    CREATED,
    VENUE_GENERATION_IN_PROGRESS,
    VENUE_ERROR,
    SESSION_GENERATION_IN_PROGRESS,
    SESSION_ERROR,
    READY
}
