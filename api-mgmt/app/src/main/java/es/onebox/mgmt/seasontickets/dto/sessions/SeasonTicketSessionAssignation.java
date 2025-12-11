package es.onebox.mgmt.seasontickets.dto.sessions;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionValidationReason;

import java.io.Serializable;

public class SeasonTicketSessionAssignation implements Serializable {

    private static final long serialVersionUID = 4094248811054185698L;

    @JsonProperty("season_ticket_id")
    private Long seasonTicketId;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("session_assigned")
    private Boolean sessionAssigned;

    @JsonProperty("reason")
    private SeasonTicketSessionValidationReason reason;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getSessionAssigned() {
        return sessionAssigned;
    }

    public void setSessionAssigned(Boolean sessionAssigned) {
        this.sessionAssigned = sessionAssigned;
    }

    public SeasonTicketSessionValidationReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketSessionValidationReason reason) {
        this.reason = reason;
    }
}
