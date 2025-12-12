package es.onebox.common.datasources.common.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

public class Charges implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal channel;

    private BigDecimal promoter;

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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
