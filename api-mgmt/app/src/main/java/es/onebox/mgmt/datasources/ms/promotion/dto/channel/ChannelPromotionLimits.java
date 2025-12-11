package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionLimit;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionMaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionLimits implements Serializable {

    private static final long serialVersionUID = 6900105671786456825L;

    private PromotionMaxLimit promotionMaxLimit;
    private PromotionLimit purchaseMinLimit;
    private ChannelPromotionAmount amountMinLimit;

    public PromotionMaxLimit getPromotionMaxLimit() {
        return promotionMaxLimit;
    }

    public void setPromotionMaxLimit(PromotionMaxLimit promotionMaxLimit) {
        this.promotionMaxLimit = promotionMaxLimit;
    }

    public PromotionLimit getPurchaseMinLimit() {
        return purchaseMinLimit;
    }

    public void setPurchaseMinLimit(PromotionLimit purchaseMinLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
    }

    public ChannelPromotionAmount getAmountMinLimit() {
        return amountMinLimit;
    }

    public void setAmountMinLimit(ChannelPromotionAmount amountMinLimit) {
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
