package es.onebox.event.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CatalogPrices implements Serializable {

    @Serial
    private static final long serialVersionUID = 4069984761140363098L;

    private CatalogPrice minBasePrice;
    private CatalogPrice maxBasePrice;
    private CatalogPromotedPrice minPromotedPrice;
    private CatalogPrice minNetPrice;
    private CatalogPrice maxNetPrice;
    private CatalogPromotedPrice minNetPromotedPrice;
    private CatalogPrice minFinalPrice;
    private CatalogPrice maxFinalPrice;
    private CatalogPrice minFinalPromotedPrice;

    public CatalogPrice getMinBasePrice() {
        return minBasePrice;
    }

    public void setMinBasePrice(CatalogPrice minBasePrice) {
        this.minBasePrice = minBasePrice;
    }

    public CatalogPrice getMaxBasePrice() {
        return maxBasePrice;
    }

    public void setMaxBasePrice(CatalogPrice maxBasePrice) {
        this.maxBasePrice = maxBasePrice;
    }

    public CatalogPromotedPrice getMinPromotedPrice() {
        return minPromotedPrice;
    }

    public void setMinPromotedPrice(CatalogPromotedPrice minPromotedPrice) {
        this.minPromotedPrice = minPromotedPrice;
    }

    public CatalogPrice getMinNetPrice() {
        return minNetPrice;
    }

    public void setMinNetPrice(CatalogPrice minNetPrice) {
        this.minNetPrice = minNetPrice;
    }

    public CatalogPrice getMaxNetPrice() {
        return maxNetPrice;
    }

    public void setMaxNetPrice(CatalogPrice maxNetPrice) {
        this.maxNetPrice = maxNetPrice;
    }

    public CatalogPromotedPrice getMinNetPromotedPrice() {
        return minNetPromotedPrice;
    }

    public void setMinNetPromotedPrice(CatalogPromotedPrice minNetPromotedPrice) {
        this.minNetPromotedPrice = minNetPromotedPrice;
    }

    public CatalogPrice getMinFinalPrice() {
        return minFinalPrice;
    }

    public void setMinFinalPrice(CatalogPrice minFinalPrice) {
        this.minFinalPrice = minFinalPrice;
    }

    public CatalogPrice getMaxFinalPrice() {
        return maxFinalPrice;
    }

    public void setMaxFinalPrice(CatalogPrice maxFinalPrice) {
        this.maxFinalPrice = maxFinalPrice;
    }

    public CatalogPrice getMinFinalPromotedPrice() {
        return minFinalPromotedPrice;
    }

    public void setMinFinalPromotedPrice(CatalogPrice minFinalPromotedPrice) {
        this.minFinalPromotedPrice = minFinalPromotedPrice;
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
