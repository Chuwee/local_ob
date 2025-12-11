package es.onebox.mgmt.products.promotions.dto;

import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

@Valid
public class UpdateProductPromotionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8451117416260512825L;

    @Size(min = 1, max = 40, message = "name must have between 1 and 40 characters")
    private String name;
    private PromotionStatus status;
    private ProductPromotionDiscountDTO discount;
    private UpdateProductPromotionActivatorDTO activator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public ProductPromotionDiscountDTO getDiscount() {
        return discount;
    }

    public void setDiscount(ProductPromotionDiscountDTO discount) {
        this.discount = discount;
    }

    public UpdateProductPromotionActivatorDTO getActivator() {
        return activator;
    }

    public void setActivator(UpdateProductPromotionActivatorDTO activator) {
        this.activator = activator;
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
