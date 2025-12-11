package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;

public class RateRestrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = 253609915189981338L;

    private RateDatesRestriction rateDatesRestriction;
    private RateCustomerTypesRestriction rateCustomerTypesRestriction;
    private RateRelationsRestriction relationRestriction;
    private RatePriceZonesRestriction ratePriceZonesRestriction;
    private RateChannelRestriction rateChannelRestriction;
    private RatePeriodRestriction ratePeriodRestriction;
    private Integer maxItemRestriction;

    public RateDatesRestriction getRateDatesRestriction() {
        return rateDatesRestriction;
    }

    public void setRateDatesRestriction(RateDatesRestriction rateDatesRestriction) {
        this.rateDatesRestriction = rateDatesRestriction;
    }

    public RateCustomerTypesRestriction getRateCustomerTypesRestriction() {
        return rateCustomerTypesRestriction;
    }

    public void setRateCustomerTypesRestriction(RateCustomerTypesRestriction rateCustomerTypesRestriction) {
        this.rateCustomerTypesRestriction = rateCustomerTypesRestriction;
    }

    public RateRelationsRestriction getRelationRestriction() {
        return relationRestriction;
    }

    public void setRelationRestriction(RateRelationsRestriction relationRestriction) {
        this.relationRestriction = relationRestriction;
    }

    public RatePriceZonesRestriction getRatePriceZonesRestriction() {
        return ratePriceZonesRestriction;
    }

    public void setRatePriceZonesRestriction(RatePriceZonesRestriction ratePriceZonesRestriction) {
        this.ratePriceZonesRestriction = ratePriceZonesRestriction;
    }

    public RateChannelRestriction getRateChannelRestriction() {
        return rateChannelRestriction;
    }

    public void setRateChannelRestriction(RateChannelRestriction rateChannelRestriction) {
        this.rateChannelRestriction = rateChannelRestriction;
    }

    public RatePeriodRestriction getRatePeriodRestriction() {
        return ratePeriodRestriction;
    }

    public void setRatePeriodRestriction(RatePeriodRestriction ratePeriodRestriction) {
        this.ratePeriodRestriction = ratePeriodRestriction;
    }

    public Integer getMaxItemRestriction() {
        return maxItemRestriction;
    }

    public void setMaxItemRestriction(Integer maxItemRestriction) {
        this.maxItemRestriction = maxItemRestriction;
    }
}
