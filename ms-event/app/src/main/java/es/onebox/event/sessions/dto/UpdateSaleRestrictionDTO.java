package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

public class UpdateSaleRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private List<Long> requiredPriceTypeIds;

    private Integer requiredTicketsNumber;

    private Integer lockedTicketsNumber;

    public List<Long> getRequiredPriceTypeIds() {
        return requiredPriceTypeIds;
    }

    public void setRequiredPriceTypeIds(List<Long> requiredPriceTypeIds) {
        this.requiredPriceTypeIds = requiredPriceTypeIds;
    }

    public Integer getRequiredTicketsNumber() {
        return requiredTicketsNumber;
    }

    public void setRequiredTicketsNumber(Integer requiredTicketsNumber) {
        this.requiredTicketsNumber = requiredTicketsNumber;
    }

    public Integer getLockedTicketsNumber() {
        return lockedTicketsNumber;
    }

    public void setLockedTicketsNumber(Integer lockedTicketsNumber) {
        this.lockedTicketsNumber = lockedTicketsNumber;
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
