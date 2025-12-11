package es.onebox.mgmt.seasontickets.dto.sessions;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionUnAssignationReason;

import java.io.Serializable;

public class SeasonTicketSessionUnAssignation implements Serializable {

    private static final long serialVersionUID = 4094248811054185698L;

    @JsonProperty("season_ticket_id")
    private Long seasonTicketId;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("session_unassigned")
    private Boolean sessionUnAssigned;

    @JsonProperty("reason")
    private SeasonTicketSessionUnAssignationReason reason;

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

    public Boolean getSessionUnAssigned() {
        return sessionUnAssigned;
    }

    public void setSessionUnAssigned(Boolean sessionAssigned) {
        this.sessionUnAssigned = sessionAssigned;
    }

    public SeasonTicketSessionUnAssignationReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketSessionUnAssignationReason reason) {
        this.reason = reason;
    }
}
