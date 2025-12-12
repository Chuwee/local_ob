package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.PackPricingType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -181826856077863128L;

    private Boolean active;
    private PackPricingType pricingType;
    private Double priceIncrement;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PackPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PackPricingType pricingType) {
        this.pricingType = pricingType;
    }

    public Double getPriceIncrement() {
        return priceIncrement;
    }

    public void setPriceIncrement(Double priceIncrement) {
        this.priceIncrement = priceIncrement;
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
