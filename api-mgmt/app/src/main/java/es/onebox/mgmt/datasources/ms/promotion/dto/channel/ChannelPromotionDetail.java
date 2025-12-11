package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPromotionDetail extends ChannelPromotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 4825452727039162031L;

    private Boolean combinable;
    private ChannelPromotionCollectiveDetail collective;
    private ChannelPromotionDiscount discount;
    private ChannelPromotionLimits limits;
    private ChannelPromotionPacks packs;

    public Boolean getCombinable() {
        return combinable;
    }

    public void setCombinable(Boolean combinable) {
        this.combinable = combinable;
    }

    public ChannelPromotionCollectiveDetail getCollective() {
        return collective;
    }

    public void setCollective(ChannelPromotionCollectiveDetail collective) {
        this.collective = collective;
    }

    public ChannelPromotionDiscount getDiscount() {
        return discount;
    }

    public void setDiscount(ChannelPromotionDiscount discount) {
        this.discount = discount;
    }

    public ChannelPromotionLimits getLimits() {
        return limits;
    }

    public void setLimits(ChannelPromotionLimits limits) {
        this.limits = limits;
    }

    public ChannelPromotionPacks getPacks() {
        return packs;
    }

    public void setPacks(ChannelPromotionPacks packs) {
        this.packs = packs;
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
