package es.onebox.mgmt.products.promotions.dto;

import es.onebox.mgmt.products.promotions.enums.ProductPromotionActivator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductPromotionActivatorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1996584014292786874L;

    private ProductPromotionActivator type;
    private ProductPromotionCollectiveDetailDTO collective;

    public ProductPromotionActivator getType() {
        return type;
    }

    public void setType(ProductPromotionActivator type) {
        this.type = type;
    }

    public ProductPromotionCollectiveDetailDTO getCollective() {
        return collective;
    }

    public void setCollective(ProductPromotionCollectiveDetailDTO collective) {
        this.collective = collective;
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
