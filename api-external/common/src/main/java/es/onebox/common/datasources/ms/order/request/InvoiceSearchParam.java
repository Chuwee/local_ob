package es.onebox.common.datasources.ms.order.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class InvoiceSearchParam implements Serializable {
    private static final long serialVersionUID = 4434266018606564260L;

    private ZonedDateTime invoiceGenerationDateFrom;
    private ZonedDateTime invoiceGenerationDateTo;
    private List<Long> channelIds;
    private Pagination pagination;
    private Long entityId;
    private String invoiceCode;

    public ZonedDateTime getInvoiceGenerationDateFrom() {
        return invoiceGenerationDateFrom;
    }

    public void setInvoiceGenerationDateFrom(ZonedDateTime invoiceGenerationDateFrom) {
        this.invoiceGenerationDateFrom = invoiceGenerationDateFrom;
    }

    public ZonedDateTime getInvoiceGenerationDateTo() {
        return invoiceGenerationDateTo;
    }

    public void setInvoiceGenerationDateTo(ZonedDateTime invoiceGenerationDateTo) {
        this.invoiceGenerationDateTo = invoiceGenerationDateTo;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
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
