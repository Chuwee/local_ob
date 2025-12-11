package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.common.ticketcontents.BaseTicketTemplateDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventTicketTemplates extends BaseTicketTemplateDTO {

    private static final long serialVersionUID = 2L;

    private Long individualTicketPdfTemplateId;
    private Long individualTicketPrinterTemplateId;
    private String individualTicketPassbookTemplateCode;

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
