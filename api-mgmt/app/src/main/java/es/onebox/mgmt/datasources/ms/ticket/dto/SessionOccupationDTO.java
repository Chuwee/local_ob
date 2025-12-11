package es.onebox.mgmt.datasources.ms.ticket.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class SessionOccupationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<TicketStatus, Long> status;

    private Boolean unlimited;

    public Map<TicketStatus, Long> getStatus() {
        return status;
    }

    public void setStatus(Map<TicketStatus, Long> status) {
        this.status = status;
    }

    public Boolean getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(Boolean unlimited) {
        this.unlimited = unlimited;
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
