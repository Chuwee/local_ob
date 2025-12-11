package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductCatalogVariantPriceTaxes implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double total;
    private List<ProductCatalogVariantPriceTaxesBreakdown> breakdown;


    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<ProductCatalogVariantPriceTaxesBreakdown> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(List<ProductCatalogVariantPriceTaxesBreakdown> breakdown) {
        this.breakdown = breakdown;
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
