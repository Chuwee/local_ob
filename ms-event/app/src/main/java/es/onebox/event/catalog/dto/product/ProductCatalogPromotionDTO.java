package es.onebox.event.catalog.dto.product;

import es.onebox.event.products.dao.couch.ProductPromotionActivator;
import es.onebox.event.products.dao.couch.ProductPromotionDiscountType;
import es.onebox.event.promotions.enums.PromotionType;

import java.io.Serializable;

public class ProductCatalogPromotionDTO implements Serializable {

    private static final long serialVersionUID = 2820104153543850505L;

    private Long id;
    private String name;
    private PromotionType type;
    private ProductPromotionDiscountType discountType;
    private Double discountValue;
    private ProductPromotionActivator activator;
    private String activatorId;

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

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
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
}
