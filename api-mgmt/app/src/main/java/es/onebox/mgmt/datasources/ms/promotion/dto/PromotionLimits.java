package es.onebox.mgmt.datasources.ms.promotion.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PromotionLimits implements Serializable {

    private static final long serialVersionUID = 3L;

    private PromotionLimit purchaseMinLimit;
    private PromotionLimit purchaseMaxLimit;
    private PromotionMaxLimit promotionMaxLimit;
    private PromotionLimit sessionMaxLimit;
    private PromotionLimit packs;
    private PromotionLimit sessionUserCollectiveMaxLimit;

    private PromotionLimit eventUserCollectiveMaxLimit;

    public PromotionLimits() {
    }

    public PromotionLimits(PromotionLimit purchaseMinLimit, PromotionLimit purchaseMaxLimit,
            PromotionMaxLimit promotionMaxLimit, PromotionLimit sessionMaxLimit, PromotionLimit packs) {
        this.purchaseMinLimit = purchaseMinLimit;
        this.purchaseMaxLimit = purchaseMaxLimit;
        this.promotionMaxLimit = promotionMaxLimit;
        this.sessionMaxLimit = sessionMaxLimit;
        this.packs = packs;
    }

    public PromotionLimit getPurchaseMinLimit() {
        return purchaseMinLimit;
    }


    public void setPurchaseMinLimit(PromotionLimit purchaseMinLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
    }


    public PromotionLimit getPurchaseMaxLimit() {
        return purchaseMaxLimit;
    }


    public void setPurchaseMaxLimit(PromotionLimit purchaseMaxLimit) {
        this.purchaseMaxLimit = purchaseMaxLimit;
    }


    public PromotionMaxLimit getPromotionMaxLimit() {
        return promotionMaxLimit;
    }


    public void setPromotionMaxLimit(PromotionMaxLimit promotionMaxLimit) {
        this.promotionMaxLimit = promotionMaxLimit;
    }


    public PromotionLimit getSessionMaxLimit() {
        return sessionMaxLimit;
    }


    public void setSessionMaxLimit(PromotionLimit sessionMaxLimit) {
        this.sessionMaxLimit = sessionMaxLimit;
    }


    public PromotionLimit getPacks() {
        return packs;
    }


    public void setPacks(PromotionLimit packs) {
        this.packs = packs;
    }

    public PromotionLimit getSessionUserCollectiveMaxLimit() {
        return sessionUserCollectiveMaxLimit;
    }

    public void setSessionUserCollectiveMaxLimit(PromotionLimit sessionUserCollectiveMaxLimit) {
        this.sessionUserCollectiveMaxLimit = sessionUserCollectiveMaxLimit;
    }

    public PromotionLimit getEventUserCollectiveMaxLimit() {
        return eventUserCollectiveMaxLimit;
    }

    public void setEventUserCollectiveMaxLimit(PromotionLimit eventUserCollectiveMaxLimit) {
        this.eventUserCollectiveMaxLimit = eventUserCollectiveMaxLimit;
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
