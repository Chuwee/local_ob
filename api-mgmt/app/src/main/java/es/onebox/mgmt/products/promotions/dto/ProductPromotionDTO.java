package es.onebox.mgmt.products.promotions.dto;

import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.products.promotions.enums.ProductPromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductPromotionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private PromotionStatus status;
    private ProductPromotionType type;
    private ProductPromotionDiscountDTO discount;
    private ProductPromotionActivatorDTO activator;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public ProductPromotionType getType() {
        return type;
    }

    public void setType(ProductPromotionType type) {
        this.type = type;
    }

    public ProductPromotionDiscountDTO getDiscount() {
        return discount;
    }

    public void setDiscount(ProductPromotionDiscountDTO discount) {
        this.discount = discount;
    }

    public ProductPromotionActivatorDTO getActivator() {
        return activator;
    }

    public void setActivator(ProductPromotionActivatorDTO activator) {
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
