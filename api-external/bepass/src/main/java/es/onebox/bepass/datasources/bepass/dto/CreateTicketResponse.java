package es.onebox.bepass.datasources.bepass.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateTicketResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String status;
    private List<String> ticketCreated;
    private List<String> ticketsAlreadyInDataBase;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTicketCreated() {
        return ticketCreated;
    }

    public void setTicketCreated(List<String> ticketCreated) {
        this.ticketCreated = ticketCreated;
    }

    public List<String> getTicketsAlreadyInDataBase() {
        return ticketsAlreadyInDataBase;
    }

    public void setTicketsAlreadyInDataBase(List<String> ticketsAlreadyInDataBase) {
        this.ticketsAlreadyInDataBase = ticketsAlreadyInDataBase;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
