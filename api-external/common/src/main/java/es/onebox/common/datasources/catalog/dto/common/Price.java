package es.onebox.common.datasources.catalog.dto.common;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Price implements Serializable {

    @Serial
    private static final long serialVersionUID = -269451818125291325L;

    private Double value;
    private PriceSurcharge surcharge;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public PriceSurcharge getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(PriceSurcharge surcharge) {
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
