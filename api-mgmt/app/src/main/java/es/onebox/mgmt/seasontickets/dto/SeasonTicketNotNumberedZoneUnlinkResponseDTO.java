package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketNotNumberedZoneUnlinkResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("unlinked_seats")
    private Long unlinkedSeats;

    public Long getUnlinkedSeats() {
        return unlinkedSeats;
    }

    public void setUnlinkedSeats(Long unlinkedSeats) {
        this.unlinkedSeats = unlinkedSeats;
    }
}
