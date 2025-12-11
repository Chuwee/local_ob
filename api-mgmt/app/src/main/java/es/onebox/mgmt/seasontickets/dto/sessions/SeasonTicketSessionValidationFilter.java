package es.onebox.mgmt.seasontickets.dto.sessions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketSessionValidationFilter implements Serializable {
    private static final long serialVersionUID = 286743294586889904L;

    @JsonProperty("sessions")
    private List<Long> sessionList;

    public List<Long> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Long> sessionList) {
        this.sessionList = sessionList;
    }
}
