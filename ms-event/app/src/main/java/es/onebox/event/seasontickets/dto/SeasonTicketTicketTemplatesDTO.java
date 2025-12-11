package es.onebox.event.seasontickets.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.common.dto.BaseTicketTemplatesDTO;

public class SeasonTicketTicketTemplatesDTO extends BaseTicketTemplatesDTO {

    private static final long serialVersionUID = 1L;

    private Long ticketPdfTemplateId;
    private Long ticketPrinterTemplateId;


    public Long getTicketPdfTemplateId() {
        return ticketPdfTemplateId;
    }

    public void setTicketPdfTemplateId(Long ticketPdfTemplateId) {
        this.ticketPdfTemplateId = ticketPdfTemplateId;
    }

    public Long getTicketPrinterTemplateId() {
        return ticketPrinterTemplateId;
    }

    public void setTicketPrinterTemplateId(Long ticketPrinterTemplateId) {
        this.ticketPrinterTemplateId = ticketPrinterTemplateId;
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
