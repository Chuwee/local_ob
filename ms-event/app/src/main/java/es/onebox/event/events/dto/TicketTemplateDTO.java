package es.onebox.event.events.dto;

import es.onebox.event.events.enums.TicketFormat;

public class TicketTemplateDTO {
    private Long id;
    private String name;
    private TicketFormat ticketFormat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TicketFormat getTicketFormat() {
        return ticketFormat;
    }

    public void setTicketFormat(TicketFormat ticketFormat) {
        this.ticketFormat = ticketFormat;
    }

}
