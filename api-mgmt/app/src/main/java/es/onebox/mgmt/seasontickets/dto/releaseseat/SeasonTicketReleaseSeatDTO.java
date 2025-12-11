package es.onebox.mgmt.seasontickets.dto.releaseseat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class SeasonTicketReleaseSeatDTO implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    @NotNull
    @Min(0)
    @JsonProperty("session_id")
    private Long sessionId;
    @NotNull
    @Min(0)
    @JsonProperty("season_ticket_seat_id")
    private Long seasonTicketSeatId;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSeasonTicketSeatId() {
        return seasonTicketSeatId;
    }

    public void setSeasonTicketSeatId(Long seasonTicketSeatId) {
        this.seasonTicketSeatId = seasonTicketSeatId;
    }

}