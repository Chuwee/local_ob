package es.onebox.event.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CatalogPrice implements Serializable {

    private static final long serialVersionUID = -7729662351205700551L;

    private Double value;
    private CatalogPriceSurcharge surcharge;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public CatalogPriceSurcharge getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(CatalogPriceSurcharge surcharge) {
        this.surcharge = surcharge;
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
