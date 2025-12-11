package es.onebox.mgmt.datasources.ms.event.dto.session;

import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateSaleRestriction implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private List<Long> requiredPriceTypeIds;

    private Long requiredTicketsNumber;

    private Long lockedTicketsNumber;

    public List<Long> getRequiredPriceTypeIds() {
        return requiredPriceTypeIds;
    }

    public void setRequiredPriceTypeIds(List<Long> requiredPriceTypeIds) {
        this.requiredPriceTypeIds = requiredPriceTypeIds;
    }

    public Long getRequiredTicketsNumber() {
        return requiredTicketsNumber;
    }

    public void setRequiredTicketsNumber(Long requiredTicketsNumber) {
        this.requiredTicketsNumber = requiredTicketsNumber;
    }

    public Long getLockedTicketsNumber() {
        return lockedTicketsNumber;
    }

    public void setLockedTicketsNumber(Long lockedTicketsNumber) {
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
