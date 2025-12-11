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

public class PriceSurcharges implements Serializable {

    @Serial
    private static final long serialVersionUID = -3990757279970985163L;

    private Double promoter;
    private Double channel;
    private Double secondaryMarket;

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
