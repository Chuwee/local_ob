package es.onebox.mgmt.datasources.ms.promotion.dto.product;

import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

@Valid
public class UpdateProductPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(min = 1, max = 40, message = "name must have between 1 and 40 characters")
    private String name;
    private PromotionActivationStatus status;
    private ProductPromotionDiscountType discountType;
    private Double discountValue;
    private ProductPromotionActivator activator;
    private String activatorId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionActivationStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionActivationStatus status) {
        this.status = status;
    }

    public ProductPromotionDiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(ProductPromotionDiscountType discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public ProductPromotionActivator getActivator() {
        return activator;
    }

    public void setActivator(ProductPromotionActivator activator) {
        this.activator = activator;
    }

    public String getActivatorId() {
        return activatorId;
    }

    public void setActivatorId(String activatorId) {
        this.activatorId = activatorId;
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
