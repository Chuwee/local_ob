package es.onebox.common.datasources.catalog.dto.common;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PriceSurcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = -4361349650716161425L;

    private Double promoter;
    private Double channel;

    public Double getPromoter() {
        return promoter;
    }

    public void setPromoter(Double promoter) {
        this.promoter = promoter;
    }

    public Double getChannel() {
        return channel;
    }

    public void setChannel(Double channel) {
        this.channel = channel;
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
