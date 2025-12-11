package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serializable;

public class PromotionSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private PromotionDiscountConfig discountConfig;
    private PromotionScope scope;
    private Boolean accesControlRestricted;

    public PromotionDiscountConfig getDiscountConfig() {
        return discountConfig;
    }

    public void setDiscountConfig(PromotionDiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    public PromotionScope getScope() {
        return scope;
    }

    public void setScope(PromotionScope scope) {
        this.scope = scope;
    }

    public Boolean getAccesControlRestricted() {
        return accesControlRestricted;
    }

    public void setAccesControlRestricted(Boolean accesControlRestricted) {
        this.accesControlRestricted = accesControlRestricted;
    }
}
