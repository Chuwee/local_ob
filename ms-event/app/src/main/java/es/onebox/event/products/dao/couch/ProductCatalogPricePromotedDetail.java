package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductCatalogPricePromotedDetail extends ProductCatalogPriceDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double discountedValue;
    private Double originalPrice;
    private ProductPromotionDiscountType variationType;

    public Double getDiscountedValue() {
        return discountedValue;
    }

    public void setDiscountedValue(Double discountedValue) {
        this.discountedValue = discountedValue;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public ProductPromotionDiscountType getVariationType() {
        return variationType;
    }

    public void setVariationType(ProductPromotionDiscountType variationType) {
        this.variationType = variationType;
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
