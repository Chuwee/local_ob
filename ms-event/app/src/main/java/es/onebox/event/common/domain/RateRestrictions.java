package es.onebox.event.common.domain;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RateRestrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private RateDateRestriction dateRestriction;
    private List<IdNameCodeDTO> customerTypeRestriction;
    private RateRelationsRestriction rateRelationsRestriction;
    private List<Integer> priceZoneRestriction;
    private Boolean priceZoneRestrictionApplyToB2b;
    private List<Integer> channelRestriction;
    private List<String> periodRestrictions;
    private Integer maxItemRestriction;

    public RateDateRestriction getDateRestriction() {
        return dateRestriction;
    }

    public void setDateRestriction(RateDateRestriction dateRestriction) {
        this.dateRestriction = dateRestriction;
    }

    public List<IdNameCodeDTO> getCustomerTypeRestriction() {
        return customerTypeRestriction;
    }

    public void setCustomerTypeRestriction(List<IdNameCodeDTO> customerTypeRestriction) {
        this.customerTypeRestriction = customerTypeRestriction;
    }

    public RateRelationsRestriction getRateRelationsRestriction() {
        return rateRelationsRestriction;
    }

    public void setRateRelationsRestriction(
        RateRelationsRestriction rateRelationsRestriction) {
        this.rateRelationsRestriction = rateRelationsRestriction;
    }

    public List<Integer> getPriceZoneRestriction() {
        return priceZoneRestriction;
    }

    public void setPriceZoneRestriction(List<Integer> priceZoneRestriction) {
        this.priceZoneRestriction = priceZoneRestriction;
    }

    public List<Integer> getChannelRestriction() {
        return channelRestriction;
    }

    public void setChannelRestriction(List<Integer> channelRestriction) {
        this.channelRestriction = channelRestriction;
    }

    public List<String> getPeriodRestrictions() {
        return periodRestrictions;
    }

    public void setPeriodRestrictions(List<String> periodRestrictions) {
        this.periodRestrictions = periodRestrictions;
    }

    public Integer getMaxItemRestriction() {
        return maxItemRestriction;
    }

    public void setMaxItemRestriction(Integer maxItemRestriction) {
        this.maxItemRestriction = maxItemRestriction;
    }

    public Boolean getPriceZoneRestrictionApplyToB2b() {
        return priceZoneRestrictionApplyToB2b;
    }

    public void setPriceZoneRestrictionApplyToB2b(Boolean priceZoneRestrictionApplyToB2b) {
        this.priceZoneRestrictionApplyToB2b = priceZoneRestrictionApplyToB2b;
    }
}
