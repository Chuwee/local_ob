package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductCatalogVariantPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 'min' will be removed soon; kept temporarily for compatibility.
    private ProductCatalogVariantMinPrice min;

    private Double base;
    private Double net;
    private Double total;
    private ProductCatalogVariantPriceTaxes taxes;
    private List<ProductCatalogVariantPriceSurcharges> surcharges;

    public ProductCatalogVariantMinPrice getMin() {
        return min;
    }

    public void setMin(ProductCatalogVariantMinPrice min) {
        this.min = min;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public ProductCatalogVariantPriceTaxes getTaxes() {
        return taxes;
    }

    public void setTaxes(ProductCatalogVariantPriceTaxes taxes) {
        this.taxes = taxes;
    }


    public List<ProductCatalogVariantPriceSurcharges> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<ProductCatalogVariantPriceSurcharges> surcharges) {
        this.surcharges = surcharges;
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
