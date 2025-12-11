package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.mgmt.common.ticketcontents.BaseTicketTemplateDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketTicketTemplates extends BaseTicketTemplateDTO {

    private static final long serialVersionUID = 2L;

    private Long ticketPdfTemplateId;
    private Long ticketPrinterTemplateId;
    private String individualTicketPassbookTemplateCode;

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

    public String getIndividualTicketPassbookTemplateCode() {
        return individualTicketPassbookTemplateCode;
    }

    public void setIndividualTicketPassbookTemplateCode(String individualTicketPassbookTemplateCode) {
        this.individualTicketPassbookTemplateCode = individualTicketPassbookTemplateCode;
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
