package es.onebox.mgmt.datasources.ms.event.dto.externalevent;

import java.io.Serializable;

public class ExternalEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long internalId;
    private String eventId;
    private Integer entityId;
    private String eventName;
    private ExternalEventType eventType;

    public Long getInternalId() {
        return internalId;
    }

    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public ExternalEventType getEventType() {
        return eventType;
    }

    public void setEventType(ExternalEventType eventType) {
        this.eventType = eventType;
    }
}
