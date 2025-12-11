package es.onebox.mgmt.datasources.ms.event.dto.session;

import es.onebox.mgmt.sessions.dto.PriceTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionSaleRestriction implements Serializable {

    private List<PriceTypeDTO> requiredPriceTypes;

    private PriceTypeDTO lockedPriceType;

    private Integer requiredTickets;

    private Integer lockedTickets;

    public List<PriceTypeDTO> getRequiredPriceTypes() {
        return requiredPriceTypes;
    }

    public void setRequiredPriceTypes(List<PriceTypeDTO> requiredPriceTypes) {
        this.requiredPriceTypes = requiredPriceTypes;
    }

    public PriceTypeDTO getLockedPriceType() {
        return lockedPriceType;
    }

    public void setLockedPriceType(PriceTypeDTO lockedPriceType) {
        this.lockedPriceType = lockedPriceType;
    }

    public Integer getRequiredTickets() {
        return requiredTickets;
    }

    public void setRequiredTickets(Integer requiredTickets) {
        this.requiredTickets = requiredTickets;
    }

    public Integer getLockedTickets() {
        return lockedTickets;
    }

    public void setLockedTickets(Integer lockedTickets) {
        this.lockedTickets = lockedTickets;
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
