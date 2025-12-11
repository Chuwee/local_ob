package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketNotNumberedZoneLinkResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("linked_seats")
    private Long linkedSeats;
    @JsonProperty("not_linked_seats")
    private Long notLinkedSeats;

    public Long getLinkedSeats() {
        return linkedSeats;
    }

    public void setLinkedSeats(Long linkedSeats) {
        this.linkedSeats = linkedSeats;
    }

    public Long getNotLinkedSeats() {
        return notLinkedSeats;
    }

    public void setNotLinkedSeats(Long notLinkedSeats) {
        this.notLinkedSeats = notLinkedSeats;
    }
}
