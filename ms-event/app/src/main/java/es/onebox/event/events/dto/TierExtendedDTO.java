package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class TierExtendedDTO extends TierDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TierSalesGroupLimitDTO> salesGroupLimit;


    public List<TierSalesGroupLimitDTO> getSalesGroupLimit() {
        return salesGroupLimit;
    }

    public void setSalesGroupLimit(List<TierSalesGroupLimitDTO> salesGroupLimit) {
        this.salesGroupLimit = salesGroupLimit;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "TierExtendedDTO{" +
                "salesGroupLimit=" + salesGroupLimit +
                "} " + super.toString();
    }
}
