package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class OrderPricePromotionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8992244627238668347L;

    private Double automatic;
    private Double promotion;
    private Double discount;
    private Double channelAutomatic;
    private Double channelCollective;

    public Double getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Double automatic) {
        this.automatic = automatic;
    }

    public Double getPromotion() {
        return promotion;
    }

    public void setPromotion(Double promotion) {
        this.promotion = promotion;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getChannelAutomatic() {
        return channelAutomatic;
    }

    public void setChannelAutomatic(Double channelAutomatic) {
        this.channelAutomatic = channelAutomatic;
    }

    public Double getChannelCollective() {
        return channelCollective;
    }

    public void setChannelCollective(Double channelCollective) {
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
