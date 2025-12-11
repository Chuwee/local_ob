package es.onebox.mgmt.seasontickets.dto.sessions;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionValidationReason;

import java.io.Serializable;

public class SeasonTicketSessionValidation implements Serializable {

    private static final long serialVersionUID = -662303680020570945L;

    @JsonProperty("season_ticket_id")
    private Long seasonTicketId;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("session_valid")
    private Boolean sessionValid;

    @JsonProperty("reason")
    private SeasonTicketSessionValidationReason reason;

    public SeasonTicketSessionValidation() {
    }

    public SeasonTicketSessionValidation(Long seasonTicketId, Long sessionId, Boolean sessionValid, SeasonTicketSessionValidationReason reason) {
        this.seasonTicketId = seasonTicketId;
        this.sessionId = sessionId;
        this.sessionValid = sessionValid;
        this.reason = reason;
    }

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

    public Boolean getSessionValid() {
        return sessionValid;
    }

    public void setSessionValid(Boolean sessionValid) {
        this.sessionValid = sessionValid;
    }

    public SeasonTicketSessionValidationReason getReason() {
        return reason;
    }

    public void setReason(SeasonTicketSessionValidationReason reason) {
        this.reason = reason;
    }
}
