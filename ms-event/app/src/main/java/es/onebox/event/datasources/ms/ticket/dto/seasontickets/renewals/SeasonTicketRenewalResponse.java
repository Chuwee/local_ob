package es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketRenewalResponse implements Serializable {
    private static final long serialVersionUID = 360988562600501292L;

    List<RenewalSeasonTicketRenewalSeat> renewalSeats;

    public List<RenewalSeasonTicketRenewalSeat> getRenewalSeats() {
        return renewalSeats;
    }

    public void setRenewalSeats(List<RenewalSeasonTicketRenewalSeat> renewalSeats) {
        this.renewalSeats = renewalSeats;
    }
}
