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
import java.util.List;

/**
 * @author ignasi
 */
public class SurchargeRanges implements Serializable {

    @Serial
    private static final long serialVersionUID = -925985219592156922L;

    private List<SurchargeRange> main;
    private List<SurchargeRange> promotion;
    private List<SurchargeRange> invitation;
    private List<SurchargeRange> secondaryMarket;

    public List<SurchargeRange> getMain() {
        return main;
    }

    public void setMain(List<SurchargeRange> main) {
        this.main = main;
    }

    public List<SurchargeRange> getPromotion() {
        return promotion;
    }

    public void setPromotion(List<SurchargeRange> promotion) {
        this.promotion = promotion;
    }

    public List<SurchargeRange> getInvitation() {
        return invitation;
    }

    public void setInvitation(List<SurchargeRange> invitation) {
        this.invitation = invitation;
    }

    public List<SurchargeRange> getSecondaryMarket() { return secondaryMarket; }

    public void setSecondaryMarket(List<SurchargeRange> secondaryMarket) { this.secondaryMarket = secondaryMarket; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
