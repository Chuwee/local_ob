package es.onebox.event.events.dto;

import es.onebox.event.catalog.dto.ChangeSeatAllowedSessions;
import es.onebox.event.catalog.dto.ChangeSeatTickets;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ChangeSeatNewTicketSelectionDTO {

    @NotNull
    private ChangeSeatAllowedSessions allowedSessions;

    private Boolean sameDateOnly;

    @Valid
    @NotNull
    private ChangeSeatPriceDTO price;

    @NotNull
    private ChangeSeatTickets tickets;

    public ChangeSeatAllowedSessions getAllowedSessions() {
        return allowedSessions;
    }

    public void setAllowedSessions(ChangeSeatAllowedSessions  allowedSessions) {
        this.allowedSessions = allowedSessions;
    }

    public ChangeSeatPriceDTO getPrice() {
        return price;
    }

    public void setPrice(ChangeSeatPriceDTO price) {
        this.price = price;
    }

    public ChangeSeatTickets getTickets() {
        return tickets;
    }

    public void setTickets(ChangeSeatTickets tickets) {
        this.tickets = tickets;
    }

    public Boolean getSameDateOnly() {
        return sameDateOnly;
    }

    public void setSameDateOnly(Boolean sameDateOnly) {
        this.sameDateOnly = sameDateOnly;
    }
}
