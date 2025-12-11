package es.onebox.event.events.dto;

import es.onebox.event.common.enums.RatePriceZoneCriteria;

import java.io.Serializable;
import java.util.List;

public class RateRelationsRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double maxItemsMultiplier;
    private List<Integer> requiredRates;
    private List<Integer> restrictedPriceZones;
    private RatePriceZoneCriteria priceZoneCriteria;
    private Boolean applyToB2b;

    public Double getMaxItemsMultiplier() {
        return maxItemsMultiplier;
    }

    public void setMaxItemsMultiplier(Double maxItemsMultiplier) {
        this.maxItemsMultiplier = maxItemsMultiplier;
    }

    public List<Integer> getRequiredRates() {
        return requiredRates;
    }

    public void setRequiredRates(List<Integer> requiredRates) {
        this.requiredRates = requiredRates;
    }

    public List<Integer> getRestrictedPriceZones() {
        return restrictedPriceZones;
    }

    public void setRestrictedPriceZones(List<Integer> restrictedPriceZones) {
        this.restrictedPriceZones = restrictedPriceZones;
    }

    public RatePriceZoneCriteria getPriceZoneCriteria() {
        return priceZoneCriteria;
    }

    public void setPriceZoneCriteria(RatePriceZoneCriteria priceZoneCriteria) {
        this.priceZoneCriteria = priceZoneCriteria;
    }

    public Boolean getApplyToB2b() {
        return applyToB2b;
    }

    public void setApplyToB2b(Boolean applyToB2b) {
        this.applyToB2b = applyToB2b;
    }
}
