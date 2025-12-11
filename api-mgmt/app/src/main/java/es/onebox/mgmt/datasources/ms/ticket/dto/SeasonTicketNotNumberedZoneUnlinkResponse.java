package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketNotNumberedZoneUnlinkResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long unlinkedSeats;

    public SeasonTicketNotNumberedZoneUnlinkResponse() {
    }

    public SeasonTicketNotNumberedZoneUnlinkResponse(Long unlinkedSeats) {
        this.unlinkedSeats = unlinkedSeats;
    }

    public Long getUnlinkedSeats() {
        return unlinkedSeats;
    }

    public void setUnlinkedSeats(Long unlinkedSeats) {
        this.unlinkedSeats = unlinkedSeats;
    }
}
