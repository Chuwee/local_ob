package es.onebox.event.externalevents.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class ExternalEventRateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 20)
    @NotNull
    private String eventId;

    @NotNull
    private Integer entityId;

    @Size(max = 50)
    @NotNull
    private String rateName;

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

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }
}
