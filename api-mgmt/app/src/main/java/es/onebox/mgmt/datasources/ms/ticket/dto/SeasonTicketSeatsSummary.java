package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketSeatsSummary implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean hasLinkableSeats;

    public Boolean getHasLinkableSeats() {
        return hasLinkableSeats;
    }

    public void setHasLinkableSeats(Boolean hasLinkableSeats) {
        this.hasLinkableSeats = hasLinkableSeats;
    }
}
