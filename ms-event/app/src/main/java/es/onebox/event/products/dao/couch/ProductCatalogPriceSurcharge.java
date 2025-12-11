package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductCatalogPriceSurcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = -2501338034383213714L;

    private Double promoter;

    public ProductCatalogPriceSurcharge(Double promoter) {
        this.promoter = promoter;
    }

    public ProductCatalogPriceSurcharge() {
    }

    public double getPromoter() {
        return promoter;
    }

    public void setPromoter(double promoter) {
        this.promoter = promoter;
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
