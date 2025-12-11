package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serializable;

public class PromotionScope implements Serializable {

    private static final long serialVersionUID = 1L;

    private PromotionScopeFilter session;
    private PromotionScopeFilter priceType;
    private PromotionScopeFilter rate;

    public PromotionScopeFilter getSession() {
        return session;
    }

    public void setSession(PromotionScopeFilter session) {
        this.session = session;
    }

    public PromotionScopeFilter getPriceType() {
        return priceType;
    }

    public void setPriceType(PromotionScopeFilter priceType) {
        this.priceType = priceType;
    }

    public PromotionScopeFilter getRate() {
        return rate;
    }

    public void setRate(PromotionScopeFilter rate) {
        this.rate = rate;
    }
}
