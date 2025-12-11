package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatNewTicketSelection implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChangeSeatAllowedSessions allowedSessions;
    private Boolean sameDateOnly;
    private ChangeSeatPrice price;
    private ChangeSeatTickets tickets;

    public ChangeSeatAllowedSessions getAllowedSessions() {
        return allowedSessions;
    }

    public void setAllowedSessions(ChangeSeatAllowedSessions allowedSessions) {
        this.allowedSessions = allowedSessions;
    }

    public Boolean getSameDateOnly() {
        return sameDateOnly;
    }

    public void setSameDateOnly(Boolean sameDateOnly) {
        this.sameDateOnly = sameDateOnly;
    }

    public ChangeSeatPrice getPrice() {
        return price;
    }

    public void setPrice(ChangeSeatPrice price) {
        this.price = price;
    }

    public ChangeSeatTickets getTickets() {
        return tickets;
    }

    public void setTickets(ChangeSeatTickets tickets) {
        this.tickets = tickets;
    }
}
