package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketNotNumberedZoneLinkResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long linkedSeats;
    private Long notLinkedSeats;

    public SeasonTicketNotNumberedZoneLinkResponse() {
    }

    public SeasonTicketNotNumberedZoneLinkResponse(Long linkedSeats) {
        this.linkedSeats = linkedSeats;
    }

    public SeasonTicketNotNumberedZoneLinkResponse(Long linkedSeats, Long notLinkedSeats) {
        this.linkedSeats = linkedSeats;
        this.notLinkedSeats = notLinkedSeats;
    }

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
