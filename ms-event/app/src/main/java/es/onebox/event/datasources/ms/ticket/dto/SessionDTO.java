package es.onebox.event.datasources.ms.ticket.dto;

public class SessionDTO {

    private Long sessionId;
    private Long eventId;
    private Long entityId;

    public SessionDTO() {
    }

    public SessionDTO(Long sessionId, Long eventId, Long entityId) {
        this.sessionId = sessionId;
        this.eventId = eventId;
        this.entityId = entityId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
