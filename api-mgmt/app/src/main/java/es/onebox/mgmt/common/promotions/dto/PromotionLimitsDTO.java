package es.onebox.mgmt.common.promotions.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@Valid
public class PromotionLimitsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("purchase_max")
    private PromotionLimitDTO purchaseMaxLimit;
    @JsonProperty("promotion_max")
    private PromotionMaxLimitDTO promotionMaxLimit;
    @JsonProperty("session_max")
    private PromotionLimitDTO sessionMaxLimit;
    @JsonProperty("ticket_group_min")
    private PromotionLimitDTO packs;
    @JsonProperty("purchase_min")
    private PromotionLimitDTO purchaseMinLimit;
    @JsonProperty("session_user_collective_max")
    private PromotionLimitDTO sessionUserCollectiveMaxLimit;

    @JsonProperty("event_user_collective_max")
    private PromotionLimitDTO eventUserCollectiveMaxLimit;

    public PromotionLimitsDTO() {}

    public PromotionLimitsDTO(PromotionLimitDTO purchaseMaxLimit, PromotionMaxLimitDTO promotionMaxLimit, PromotionLimitDTO sessionMaxLimit,
            PromotionLimitDTO packs) {
        this.purchaseMaxLimit = purchaseMaxLimit;
        this.promotionMaxLimit = promotionMaxLimit;
        this.sessionMaxLimit = sessionMaxLimit;
        this.packs = packs;
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

    public PromotionLimitDTO getPurchaseMinLimit() {
        return purchaseMinLimit;
    }

    public void setPurchaseMinLimit(PromotionLimitDTO purchaseMinLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
