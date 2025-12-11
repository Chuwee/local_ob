package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.event.dto.event.RatePriceZoneCriteria;

import java.io.Serializable;
import java.util.List;

public class RateRelationsRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @JsonProperty("required_tickets_number")
    private Integer requiredTicketsNumber;
    @JsonProperty("locked_tickets_number")
    private Integer lockedTicketsNumber;
    @JsonProperty("required_rate_ids")
    private List<Integer> requiredRateIds;
    @JsonProperty("restricted_price_zone_ids")
    private List<Integer> restrictedPriceTypeIds;
    @JsonProperty("use_all_zone_prices")
    private Boolean useAllZonePrices;
    @JsonProperty("price_zone_criteria")
    private RatePriceZoneCriteria priceZoneCriteria;
    @JsonProperty("apply_to_b2b")
    private Boolean applyToB2b;

    public Integer getRequiredTicketsNumber() {
        return requiredTicketsNumber;
    }

    public void setRequiredTicketsNumber(Integer requiredTicketsNumber) {
        this.requiredTicketsNumber = requiredTicketsNumber;
    }

    public Integer getLockedTicketsNumber() {
        return lockedTicketsNumber;
    }

    public void setLockedTicketsNumber(Integer lockedTicketsNumber) {
        this.lockedTicketsNumber = lockedTicketsNumber;
    }

    public List<Integer> getRequiredRateIds() {
        return requiredRateIds;
    }

    public void setRequiredRateIds(List<Integer> requiredRateIds) {
        this.requiredRateIds = requiredRateIds;
    }

    public List<Integer> getRestrictedPriceTypeIds() {
        return restrictedPriceTypeIds;
    }

    public void setRestrictedPriceTypeIds(List<Integer> restrictedPriceTypeIds) {
        this.restrictedPriceTypeIds = restrictedPriceTypeIds;
    }

    public Boolean getUseAllZonePrices() {
        return useAllZonePrices;
    }

    public void setUseAllZonePrices(Boolean useAllZonePrices) {
        this.useAllZonePrices = useAllZonePrices;
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
