package es.onebox.mgmt.datasources.ms.ticket.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class SessionPriceZoneOccupationDTO implements Serializable {

    private static final long serialVersionUID = -5109175946461241240L;

    private Long priceZoneId;

    private Boolean unlimited;

    private Long limit;

    private Map<TicketStatus, Long> status;

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public Boolean getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(Boolean unlimited) {
        this.unlimited = unlimited;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Map<TicketStatus, Long> getStatus() {
        return status;
    }

    public void setStatus(Map<TicketStatus, Long> status) {
        this.status = status;
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
