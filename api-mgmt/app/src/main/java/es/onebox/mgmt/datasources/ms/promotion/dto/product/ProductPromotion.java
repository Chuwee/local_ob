package es.onebox.mgmt.datasources.ms.promotion.dto.product;

import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveType;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveValidationMethod;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PromotionActivationStatus status;
    private ProductPromotionType type;
    private ProductPromotionDiscountType discountType;
    private Double discountValue;
    private ProductPromotionActivator activator;
    private String activatorId;
    private String activatorName;
    private CollectiveType activatorType;
    private CollectiveValidationMethod activatorValidationMethod;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ProductPromotionType getType() {
        return type;
    }

    public void setType(ProductPromotionType type) {
        this.type = type;
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

    public String getActivatorName() {
        return activatorName;
    }

    public void setActivatorName(String activatorName) {
        this.activatorName = activatorName;
    }

    public CollectiveType getActivatorType() {
        return activatorType;
    }

    public void setActivatorType(CollectiveType activatorType) {
        this.activatorType = activatorType;
    }

    public CollectiveValidationMethod getActivatorValidationMethod() {
        return activatorValidationMethod;
    }

    public void setActivatorValidationMethod(CollectiveValidationMethod activatorValidationMethod) {
        this.activatorValidationMethod = activatorValidationMethod;
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
