package es.onebox.event.externalevents.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class ExternalEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long internalId;

    @Size(max = 20)
    @NotNull
    private String eventId;

    @NotNull
    private Integer entityId;

    @Size(max = 50)
    @NotNull
    private String eventName;

    @NotNull
    private ExternalEventTypeDTO eventType;

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

    public ExternalEventTypeDTO getEventType() {
        return eventType;
    }

    public void setEventType(ExternalEventTypeDTO eventType) {
        this.eventType = eventType;
    }
}
