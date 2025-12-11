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

public class RatePrices implements Serializable {

    @Serial
    private static final long serialVersionUID = -4730333215425470361L;

    private Long id;
    private Boolean defaultRate;
    private Price ratePrice;
    private List<PromotedPrice> promotedPrices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(Boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public Price getRatePrice() {
        return ratePrice;
    }

    public void setRatePrice(Price ratePrice) {
        this.ratePrice = ratePrice;
    }

    public List<PromotedPrice> getPromotedPrices() {
        return promotedPrices;
    }

    public void setPromotedPrices(List<PromotedPrice> promotedPrices) {
        this.promotedPrices = promotedPrices;
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
