package es.onebox.common.datasources.webhook.dto.fever.promotion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Valid
@JsonNaming(SnakeCaseStrategy.class)
public class PromotionLimitsFeverDTO implements Serializable {

    private static final long serialVersionUID = 3L;

    private PromotionLimitFeverDTO purchaseMinLimit;
    private PromotionLimitFeverDTO purchaseMaxLimit;
    private PromotionMaxLimitFeverDTO promotionMaxLimit;
    private PromotionLimitFeverDTO sessionMaxLimit;
    private PromotionLimitFeverDTO packs;
    private PromotionLimitFeverDTO sessionUserCollectiveMaxLimit;
    private PromotionLimitFeverDTO eventUserCollectiveMaxLimit;

    public PromotionLimitsFeverDTO() {
    }

    public PromotionLimitsFeverDTO(
        PromotionLimitFeverDTO purchaseMinLimit, PromotionLimitFeverDTO purchaseMaxLimit,
                              PromotionMaxLimitFeverDTO promotionMaxLimit, PromotionLimitFeverDTO sessionMaxLimit, PromotionLimitFeverDTO packs,
                              PromotionLimitFeverDTO sessionUserCollectiveLimit, PromotionLimitFeverDTO eventUserCollectiveLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
        this.purchaseMaxLimit = purchaseMaxLimit;
        this.promotionMaxLimit = promotionMaxLimit;
        this.sessionMaxLimit = sessionMaxLimit;
        this.packs = packs;
        this.sessionUserCollectiveMaxLimit = sessionUserCollectiveLimit;
        this.eventUserCollectiveMaxLimit = eventUserCollectiveLimit;
    }

    public PromotionLimitsFeverDTO(PromotionLimitFeverDTO purchaseMaxLimit,
                              PromotionMaxLimitFeverDTO promotionMaxLimit, PromotionLimitFeverDTO sessionMaxLimit, PromotionLimitFeverDTO packs) {
        this.purchaseMaxLimit = purchaseMaxLimit;
        this.promotionMaxLimit = promotionMaxLimit;
        this.sessionMaxLimit = sessionMaxLimit;
        this.packs = packs;
    }

    public PromotionLimitFeverDTO getPurchaseMinLimit() {
        return purchaseMinLimit;
    }


    public void setPurchaseMinLimit(PromotionLimitFeverDTO purchaseMinLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
    }


    public PromotionLimitFeverDTO getPurchaseMaxLimit() {
        return purchaseMaxLimit;
    }


    public void setPurchaseMaxLimit(PromotionLimitFeverDTO purchaseMaxLimit) {
        this.purchaseMaxLimit = purchaseMaxLimit;
    }


    public PromotionMaxLimitFeverDTO getPromotionMaxLimit() {
        return promotionMaxLimit;
    }


    public void setPromotionMaxLimit(PromotionMaxLimitFeverDTO promotionMaxLimit) {
        this.promotionMaxLimit = promotionMaxLimit;
    }


    public PromotionLimitFeverDTO getSessionMaxLimit() {
        return sessionMaxLimit;
    }


    public void setSessionMaxLimit(PromotionLimitFeverDTO sessionMaxLimit) {
        this.sessionMaxLimit = sessionMaxLimit;
    }


    public PromotionLimitFeverDTO getPacks() {
        return packs;
    }


    public void setPacks(PromotionLimitFeverDTO packs) {
        this.packs = packs;
    }

    public PromotionLimitFeverDTO getSessionUserCollectiveMaxLimit() {
        return sessionUserCollectiveMaxLimit;
    }

    public void setSessionUserCollectiveMaxLimit(
        PromotionLimitFeverDTO sessionUserCollectiveMaxLimit) {
        this.sessionUserCollectiveMaxLimit = sessionUserCollectiveMaxLimit;
    }

    public PromotionLimitFeverDTO getEventUserCollectiveMaxLimit() {
        return eventUserCollectiveMaxLimit;
    }

    public void setEventUserCollectiveMaxLimit(PromotionLimitFeverDTO eventUserCollectiveMaxLimit) {
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
