package es.onebox.mgmt.seasontickets.dto.sessions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SeasonTicketSessionsEventResponse implements Serializable {

    private static final long serialVersionUID = 2976880878664724547L;

    @JsonProperty("id")
    private Long eventId;

    @JsonProperty("name")
    private String eventName;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
