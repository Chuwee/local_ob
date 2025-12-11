package es.onebox.event.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CatalogPriceSurcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = -2501338034383213714L;

    private Double promoter;
    private Double channel;
    private Double secondaryMarket;

    public Double getPromoter() {
        return promoter;
    }

    public void setPromoter(Double promoter) { this.promoter = promoter; }

    public Double getChannel() {
        return channel;
    }

    public void setChannel(Double channel) {
        this.channel = channel;
    }

    public Double getSecondaryMarket() { return secondaryMarket; }

    public void setSecondaryMarket(Double secondaryMarket) { this.secondaryMarket = secondaryMarket; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
