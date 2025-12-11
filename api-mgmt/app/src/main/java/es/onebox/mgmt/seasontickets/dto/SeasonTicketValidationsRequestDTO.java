package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketValidationsRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("has_linkable_seats")
    private Boolean hasLinkableSeats;

    @JsonProperty("has_assigned_sessions")
    private Boolean hasAssignedSessions;

    @JsonProperty("has_pending_renewals")
    private Boolean hasPendingRenewals;

    public Boolean getHasLinkableSeats() {
        return hasLinkableSeats;
    }

    public void setHasLinkableSeats(Boolean hasLinkableSeats) {
        this.hasLinkableSeats = hasLinkableSeats;
    }

    public Boolean getHasAssignedSessions() {
        return hasAssignedSessions;
    }

    public void setHasAssignedSessions(Boolean hasAssignedSessions) {
        this.hasAssignedSessions = hasAssignedSessions;
    }

    public Boolean getHasPendingRenewals() {
        return hasPendingRenewals;
    }

    public void setHasPendingRenewals(Boolean hasPendingRenewals) {
        this.hasPendingRenewals = hasPendingRenewals;
    }
}
