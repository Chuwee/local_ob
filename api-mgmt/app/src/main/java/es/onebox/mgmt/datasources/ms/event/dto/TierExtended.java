package es.onebox.mgmt.datasources.ms.event.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class TierExtended extends Tier implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TierSalesGroupLimit> salesGroupLimit;


    public List<TierSalesGroupLimit> getSalesGroupLimit() {
        return salesGroupLimit;
    }

    public void setSalesGroupLimit(List<TierSalesGroupLimit> salesGroupLimit) {
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
        return "TierExtended{" +
                "salesGroupLimit=" + salesGroupLimit +
                "} " + super.toString();
    }
}
