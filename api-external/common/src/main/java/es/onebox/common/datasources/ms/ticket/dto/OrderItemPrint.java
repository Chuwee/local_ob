package es.onebox.common.datasources.ms.ticket.dto;


import es.onebox.common.datasources.ms.ticket.enums.PrintMode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class OrderItemPrint implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private PrintMode printMode;
    private List<TicketItemPrintDTO> tickets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrintMode getPrintMode() {
        return printMode;
    }

    public void setPrintMode(PrintMode printMode) {
        this.printMode = printMode;
    }

    public List<TicketItemPrintDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketItemPrintDTO> tickets) {
        this.tickets = tickets;
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
