package es.onebox.mgmt.externalevents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class ExternalEventsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("data")
    private List<ExternalEventDTO> externalEvents;

    public ExternalEventsResponse(List<ExternalEventDTO> externalEvents) {
        this.externalEvents = externalEvents;
    }

    public List<ExternalEventDTO> getExternalEvents() {
        return externalEvents;
    }

    public void setExternalEvents(List<ExternalEventDTO> externalEvents) {
        this.externalEvents = externalEvents;
    }
}
