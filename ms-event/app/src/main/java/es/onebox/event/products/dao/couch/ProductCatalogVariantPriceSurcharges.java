package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductCatalogVariantPriceSurcharges implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double value;
    private Double net;
    private ProductCatalogVariantPriceTaxes taxes;
    private ProductSurchargeType type;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public ProductCatalogVariantPriceTaxes getTaxes() {
        return taxes;
    }

    public void setTaxes(ProductCatalogVariantPriceTaxes taxes) {
        this.taxes = taxes;
    }

    public ProductSurchargeType getType() {
        return type;
    }

    public void setType(ProductSurchargeType type) {
        this.type = type;
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
