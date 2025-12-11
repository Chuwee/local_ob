package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.PromotionLimitDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionMaxLimitDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionLimitsDTO implements Serializable {

    private static final long serialVersionUID = -6723586321648512737L;

    @JsonProperty("promotion_max")
    private PromotionMaxLimitDTO promotionMaxLimit;

    @JsonProperty("purchase_min")
    private PromotionLimitDTO purchaseMinLimit;

    @JsonProperty("amount_min")
    private ChannelPromotionAmountDTO amountMinLimit;

    public PromotionMaxLimitDTO getPromotionMaxLimit() {
        return promotionMaxLimit;
    }

    public void setPromotionMaxLimit(PromotionMaxLimitDTO promotionMaxLimit) {
        this.promotionMaxLimit = promotionMaxLimit;
    }

    public PromotionLimitDTO getPurchaseMinLimit() {
        return purchaseMinLimit;
    }

    public void setPurchaseMinLimit(PromotionLimitDTO purchaseMinLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
    }

    public ChannelPromotionAmountDTO getAmountMinLimit() {
        return amountMinLimit;
    }

    public void setAmountMinLimit(ChannelPromotionAmountDTO amountMinLimit) {
        this.amountMinLimit = amountMinLimit;
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
