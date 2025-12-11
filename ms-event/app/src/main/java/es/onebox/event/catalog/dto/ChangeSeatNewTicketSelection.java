package es.onebox.event.catalog.dto;

import java.io.Serializable;

public class ChangeSeatNewTicketSelection implements Serializable {

    private ChangeSeatAllowedSessions allowedSessions;
    private ChangeSeatPrice price;
    private Boolean sameDateOnly;
    private ChangeSeatTickets tickets;

    public ChangeSeatAllowedSessions getAllowedSessions() {
        return allowedSessions;
    }

    public void setAllowedSessions(ChangeSeatAllowedSessions changeSeatAllowedSessions) {
        this.allowedSessions = changeSeatAllowedSessions;
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

    public void setTickets(ChangeSeatTickets changeSeatTickets) {
        this.tickets = changeSeatTickets;
    }

    public Boolean getSameDateOnly() {
        return sameDateOnly;
    }

    public void setSameDateOnly(Boolean sameDateOnly) {
        this.sameDateOnly = sameDateOnly;
    }
}
