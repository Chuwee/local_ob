package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatNewTicketSelectionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "allowed_sessions cannot be null")
    @JsonProperty("allowed_sessions")
    private ChangeSeatAllowedSessionsDTO allowedSessions;
    @JsonProperty("same_date_only")
    private Boolean sameDateOnly;

    @Valid
    @NotNull(message = "price cannot be null")
    private ChangeSeatPriceDTO price;

    @NotNull(message = "tickets cannot be null")
    private ChangeSeatTicketsDTO tickets;

    public ChangeSeatAllowedSessionsDTO getAllowedSessions() {
        return allowedSessions;
    }

    public void setAllowedSessions(ChangeSeatAllowedSessionsDTO allowedSessions) {
        this.allowedSessions = allowedSessions;
    }

    public Boolean getSameDateOnly() {
        return sameDateOnly;
    }

    public void setSameDateOnly(Boolean sameDateOnly) {
        this.sameDateOnly = sameDateOnly;
    }

    public ChangeSeatPriceDTO getPrice() {
        return price;
    }

    public void setPrice(ChangeSeatPriceDTO price) {
        this.price = price;
    }

    public ChangeSeatTicketsDTO getTickets() {
        return tickets;
    }

    public void setTickets(ChangeSeatTicketsDTO tickets) {
        this.tickets = tickets;
    }
}
