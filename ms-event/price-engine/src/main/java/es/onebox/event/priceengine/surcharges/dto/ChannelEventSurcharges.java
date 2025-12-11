/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.priceengine.surcharges.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ignasi
 */
public class ChannelEventSurcharges implements Serializable {

    @Serial
    private static final long serialVersionUID = 2536733264382250723L;

    private SurchargeRanges promoter;
    private SurchargeRanges channel;

    public SurchargeRanges getPromoter() { return promoter; }

    public void setPromoter(SurchargeRanges promoter) {
        this.promoter = promoter;
    }

    public SurchargeRanges getChannel() {
        return channel;
    }

    public void setChannel(SurchargeRanges channel) {
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
