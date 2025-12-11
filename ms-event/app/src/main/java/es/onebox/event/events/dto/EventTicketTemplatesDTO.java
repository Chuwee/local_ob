package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.common.dto.BaseTicketTemplatesDTO;

public class EventTicketTemplatesDTO extends BaseTicketTemplatesDTO {

    private static final long serialVersionUID = 2L;

    private Long individualTicketPdfTemplateId;
    private Long individualTicketPrinterTemplateId;


    public Long getIndividualTicketPdfTemplateId() {
        return individualTicketPdfTemplateId;
    }

    public void setIndividualTicketPdfTemplateId(Long individualTicketPdfTemplateId) {
        this.individualTicketPdfTemplateId = individualTicketPdfTemplateId;
    }

    public Long getIndividualTicketPrinterTemplateId() {
        return individualTicketPrinterTemplateId;
    }

    public void setIndividualTicketPrinterTemplateId(Long individualTicketPrinterTemplateId) {
        this.individualTicketPrinterTemplateId = individualTicketPrinterTemplateId;
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
