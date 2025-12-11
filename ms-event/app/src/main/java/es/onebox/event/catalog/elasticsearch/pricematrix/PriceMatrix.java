/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.catalog.elasticsearch.pricematrix;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceMatrix implements Serializable {

    @Serial
    private static final long serialVersionUID = -7211519969857457058L;

    private List<PriceZonePrices> prices;
    private Price minBasePrice;
    private Price maxBasePrice;
    private List<PromotedPrice> minPromotedPrices;

    private List<PriceZonePrices> netPrices;
    private Price minNetPrice;
    private Price maxNetPrice;
    private List<PromotedPrice> minNetPromotedPrices;

    private Price minFinalPrice;
    private Price maxFinalPrice;
    private List<PromotedPrice> minFinalPromotedPrices;

    public Price getMinBasePrice() {
        return minBasePrice;
    }

    public void setMinBasePrice(Price minBasePrice) {
        this.minBasePrice = minBasePrice;
    }

    public Price getMaxBasePrice() {
        return maxBasePrice;
    }

    public void setMaxBasePrice(Price maxBasePrice) {
        this.maxBasePrice = maxBasePrice;
    }

    public List<PromotedPrice> getMinPromotedPrices() {
        return minPromotedPrices;
    }

    public void setMinPromotedPrices(List<PromotedPrice> minPromotedPrices) {
        this.minPromotedPrices = minPromotedPrices;
    }

    public List<PriceZonePrices> getPrices() {
        return prices;
    }

    public void setPrices(List<PriceZonePrices> prices) {
        this.prices = prices;
    }

    public List<PriceZonePrices> getNetPrices() {
        return netPrices;
    }

    public void setNetPrices(List<PriceZonePrices> netPrices) {
        this.netPrices = netPrices;
    }

    public Price getMinNetPrice() {
        return minNetPrice;
    }

    public void setMinNetPrice(Price minNetPrice) {
        this.minNetPrice = minNetPrice;
    }

    public Price getMaxNetPrice() {
        return maxNetPrice;
    }

    public void setMaxNetPrice(Price maxNetPrice) {
        this.maxNetPrice = maxNetPrice;
    }

    public List<PromotedPrice> getMinNetPromotedPrices() {
        return minNetPromotedPrices;
    }

    public void setMinNetPromotedPrices(List<PromotedPrice> minNetPromotedPrices) {
        this.minNetPromotedPrices = minNetPromotedPrices;
    }

    public Price getMinFinalPrice() {
        return minFinalPrice;
    }

    public void setMinFinalPrice(Price minFinalPrice) {
        this.minFinalPrice = minFinalPrice;
    }

    public Price getMaxFinalPrice() {
        return maxFinalPrice;
    }

    public void setMaxFinalPrice(Price maxFinalPrice) {
        this.maxFinalPrice = maxFinalPrice;
    }

    public List<PromotedPrice> getMinFinalPromotedPrices() {
        return minFinalPromotedPrices;
    }

    public void setMinFinalPromotedPrices(List<PromotedPrice> minFinalPromotedPrices) {
        this.minFinalPromotedPrices = minFinalPromotedPrices;
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
