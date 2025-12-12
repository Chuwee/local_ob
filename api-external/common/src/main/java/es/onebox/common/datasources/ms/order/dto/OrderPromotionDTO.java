package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OrderPromotionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4639424352217318112L;
    private PromotionDTO automatic;
    private PromotionDTO promotion;
    private PromotionDTO discount;
    private OrderProductChannelPromotionBaseDTO channelAutomatic;
    private OrderProductChannelPromotionCollectiveDTO channelCollective;

    public PromotionDTO getAutomatic() {
        return automatic;
    }

    public void setAutomatic(PromotionDTO automatic) {
        this.automatic = automatic;
    }

    public PromotionDTO getPromotion() {
        return promotion;
    }

    public void setPromotion(PromotionDTO promotion) {
        this.promotion = promotion;
    }

    public PromotionDTO getDiscount() {
        return discount;
    }

    public void setDiscount(PromotionDTO discount) {
        this.discount = discount;
    }

    public OrderProductChannelPromotionBaseDTO getChannelAutomatic() {
        return channelAutomatic;
    }

    public void setChannelAutomatic(OrderProductChannelPromotionBaseDTO channelAutomatic) {
        this.channelAutomatic = channelAutomatic;
    }

    public OrderProductChannelPromotionCollectiveDTO getChannelCollective() {
        return channelCollective;
    }

    public void setChannelCollective(OrderProductChannelPromotionCollectiveDTO channelCollective) {
        this.channelCollective = channelCollective;
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
