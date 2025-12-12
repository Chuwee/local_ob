package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OrderPriceChargesDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4639424352217318112L;
    private Double channel;
    private Double promoter;
    private Double promoterChannel;

    public Double getChannel() {
        return channel;
    }

    public void setChannel(Double channel) {
        this.channel = channel;
    }

    public Double getPromoter() {
        return promoter;
    }

    public void setPromoter(Double promoter) {
        this.promoter = promoter;
    }

    public Double getPromoterChannel() {
        return promoterChannel;
    }

    public void setPromoterChannel(Double promoterChannel) {
        this.promoterChannel = promoterChannel;
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
