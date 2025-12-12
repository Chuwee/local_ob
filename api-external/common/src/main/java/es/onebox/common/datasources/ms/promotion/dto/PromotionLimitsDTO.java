package es.onebox.common.datasources.ms.promotion.dto;

import java.io.Serializable;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Valid
public class PromotionLimitsDTO implements Serializable {

    private static final long serialVersionUID = 3L;

    private PromotionLimitDTO purchaseMinLimit;
    private PromotionLimitDTO purchaseMaxLimit;
    private PromotionMaxLimitDTO promotionMaxLimit;
    private PromotionLimitDTO sessionMaxLimit;
    private PromotionLimitDTO packs;
    private PromotionLimitDTO sessionUserCollectiveMaxLimit;

    private PromotionLimitDTO eventUserCollectiveMaxLimit;

    public PromotionLimitsDTO() {
    }

    public PromotionLimitsDTO(PromotionLimitDTO purchaseMinLimit, PromotionLimitDTO purchaseMaxLimit,
                              PromotionMaxLimitDTO promotionMaxLimit, PromotionLimitDTO sessionMaxLimit, PromotionLimitDTO packs,
                              PromotionLimitDTO sessionUserCollectiveLimit, PromotionLimitDTO eventUserCollectiveLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
        this.purchaseMaxLimit = purchaseMaxLimit;
        this.promotionMaxLimit = promotionMaxLimit;
        this.sessionMaxLimit = sessionMaxLimit;
        this.packs = packs;
        this.sessionUserCollectiveMaxLimit = sessionUserCollectiveLimit;
        this.eventUserCollectiveMaxLimit = eventUserCollectiveLimit;
    }

    public PromotionLimitsDTO(PromotionLimitDTO purchaseMaxLimit,
                              PromotionMaxLimitDTO promotionMaxLimit, PromotionLimitDTO sessionMaxLimit, PromotionLimitDTO packs) {
        this.purchaseMaxLimit = purchaseMaxLimit;
        this.promotionMaxLimit = promotionMaxLimit;
        this.sessionMaxLimit = sessionMaxLimit;
        this.packs = packs;
    }

    public PromotionLimitDTO getPurchaseMinLimit() {
        return purchaseMinLimit;
    }


    public void setPurchaseMinLimit(PromotionLimitDTO purchaseMinLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
    }


    public PromotionLimitDTO getPurchaseMaxLimit() {
        return purchaseMaxLimit;
    }


    public void setPurchaseMaxLimit(PromotionLimitDTO purchaseMaxLimit) {
        this.purchaseMaxLimit = purchaseMaxLimit;
    }


    public PromotionMaxLimitDTO getPromotionMaxLimit() {
        return promotionMaxLimit;
    }


    public void setPromotionMaxLimit(PromotionMaxLimitDTO promotionMaxLimit) {
        this.promotionMaxLimit = promotionMaxLimit;
    }


    public PromotionLimitDTO getSessionMaxLimit() {
        return sessionMaxLimit;
    }


    public void setSessionMaxLimit(PromotionLimitDTO sessionMaxLimit) {
        this.sessionMaxLimit = sessionMaxLimit;
    }


    public PromotionLimitDTO getPacks() {
        return packs;
    }


    public void setPacks(PromotionLimitDTO packs) {
        this.packs = packs;
    }

    public PromotionLimitDTO getSessionUserCollectiveMaxLimit() {
        return sessionUserCollectiveMaxLimit;
    }

    public void setSessionUserCollectiveMaxLimit(PromotionLimitDTO sessionUserCollectiveMaxLimit) {
        this.sessionUserCollectiveMaxLimit = sessionUserCollectiveMaxLimit;
    }

    public PromotionLimitDTO getEventUserCollectiveMaxLimit() {
        return eventUserCollectiveMaxLimit;
    }

    public void setEventUserCollectiveMaxLimit(PromotionLimitDTO eventUserCollectiveMaxLimit) {
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
