package es.onebox.common.datasources.orders.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderPriceCharges implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private BigDecimal channel;

    private BigDecimal promoter;

    private BigDecimal promoterChannel;

    public BigDecimal getChannel() {
        return channel;
    }

    public void setChannel(BigDecimal channel) {
        this.channel = channel;
    }

    public BigDecimal getPromoter() {
        return promoter;
    }

    public void setPromoter(BigDecimal promoter) {
        this.promoter = promoter;
    }

    public BigDecimal getPromoterChannel() {
        return promoterChannel;
    }

    public void setPromoterChannel(BigDecimal promoterChannel) {
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
