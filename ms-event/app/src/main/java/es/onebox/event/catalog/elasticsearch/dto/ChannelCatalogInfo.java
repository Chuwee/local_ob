package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public abstract class ChannelCatalogInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean forSale;
    private Boolean soldOut;
    private List<Long> promotions;
    private PriceMatrix prices;

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public List<Long> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Long> promotions) {
        this.promotions = promotions;
    }

    public PriceMatrix getPrices() {
        return prices;
    }

    public void setPrices(PriceMatrix prices) {
        this.prices = prices;
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
