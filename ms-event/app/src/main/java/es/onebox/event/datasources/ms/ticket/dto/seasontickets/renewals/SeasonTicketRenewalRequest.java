package es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketRenewalRequest implements Serializable {
    private static final long serialVersionUID = 8955058309489738047L;

    private List<RenewalSeasonTicketOriginSeat> originSeats;
    private Long originSeasonTicketSessionId;

    public List<RenewalSeasonTicketOriginSeat> getOriginSeats() {
        return originSeats;
    }

    public void setOriginSeats(List<RenewalSeasonTicketOriginSeat> originSeats) {
        this.originSeats = originSeats;
    }

    public Long getOriginSeasonTicketSessionId() {
        return originSeasonTicketSessionId;
    }

    public void setOriginSeasonTicketSessionId(Long originSeasonTicketSessionId) {
        this.originSeasonTicketSessionId = originSeasonTicketSessionId;
    }
}
