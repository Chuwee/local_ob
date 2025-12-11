package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductCatalogPriceDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = -7729662351205700551L;

    private Double value;
    private ProductCatalogPriceSurcharge surcharge;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public ProductCatalogPriceSurcharge getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(ProductCatalogPriceSurcharge surcharge) {
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
