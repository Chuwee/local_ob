package es.onebox.mgmt.externalevents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ExternalEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long internalId;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("entity_id")
    private Integer entityId;

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("event_type")
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
