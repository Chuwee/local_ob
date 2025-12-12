package es.onebox.fifaqatar.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.accesscontrol.dto.ACTicketDTO;

import java.io.Serializable;

public class HayyaTicket extends ACTicketDTO implements Serializable {

    private static final long serialVersionUID = -3697174429165210704L;

    @JsonProperty("ticket_holder")
    private TicketHolder ticketHolder;

    public TicketHolder getTicketHolder() {
        return ticketHolder;
    }

    public void setTicketHolder(TicketHolder ticketHolder) {
        this.ticketHolder = ticketHolder;
    }
}
