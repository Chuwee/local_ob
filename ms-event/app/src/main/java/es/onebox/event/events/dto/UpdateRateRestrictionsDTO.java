package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateRateRestrictionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3572359705249025047L;

    private Boolean dateRestrictionEnabled;
    private EventRateDateRestrictionDTO dateRestriction;

    private Boolean customerTypeRestrictionEnabled;
    private List<Long> customerTypeRestriction;

    private RateRelationsRestrictionDTO rateRelationsRestriction;
    private Boolean rateRelationsRestrictionEnabled;

    private Boolean priceZoneRestrictionEnabled;
    private RatePriceZoneRestrictionDTO priceZoneRestriction;

    private Boolean channelRestrictionEnabled;
    private List<Integer> channelRestriction;

    private Boolean periodRestrictionEnabled;
    private List<String> periodRestriction;
    private Boolean maxItemRestrictionEnabled;
    private Integer maxItemRestriction;

    public Boolean getDateRestrictionEnabled() {
        return dateRestrictionEnabled;
    }

    public void setDateRestrictionEnabled(Boolean dateRestrictionEnabled) {
        this.dateRestrictionEnabled = dateRestrictionEnabled;
    }

    public EventRateDateRestrictionDTO getDateRestriction() {
        return dateRestriction;
    }

    public void setDateRestriction(EventRateDateRestrictionDTO dateRestriction) {
        this.dateRestriction = dateRestriction;
    }

    public Boolean getCustomerTypeRestrictionEnabled() {
        return customerTypeRestrictionEnabled;
    }

    public void setCustomerTypeRestrictionEnabled(Boolean customerTypeRestrictionEnabled) {
        this.customerTypeRestrictionEnabled = customerTypeRestrictionEnabled;
    }

    public List<Long> getCustomerTypeRestriction() {
        return customerTypeRestriction;
    }

    public void setCustomerTypeRestriction(List<Long> customerTypeRestriction) {
        this.customerTypeRestriction = customerTypeRestriction;
    }

    public RateRelationsRestrictionDTO getRateRelationsRestriction() {
        return rateRelationsRestriction;
    }

    public void setRateRelationsRestriction(
        RateRelationsRestrictionDTO rateRelationsRestriction) {
        this.rateRelationsRestriction = rateRelationsRestriction;
    }

    public Boolean getRateRelationsRestrictionEnabled() {
        return rateRelationsRestrictionEnabled;
    }

    public void setRateRelationsRestrictionEnabled(Boolean rateRelationsRestrictionEnabled) {
        this.rateRelationsRestrictionEnabled = rateRelationsRestrictionEnabled;
    }

    public Boolean getPriceZoneRestrictionEnabled() {
        return priceZoneRestrictionEnabled;
    }

    public void setPriceZoneRestrictionEnabled(Boolean priceZoneRestrictionEnabled) {
        this.priceZoneRestrictionEnabled = priceZoneRestrictionEnabled;
    }

    public Boolean getChannelRestrictionEnabled() {
        return channelRestrictionEnabled;
    }

    public void setChannelRestrictionEnabled(Boolean channelRestrictionEnabled) {
        this.channelRestrictionEnabled = channelRestrictionEnabled;
    }

    public List<Integer> getChannelRestriction() {
        return channelRestriction;
    }

    public void setChannelRestriction(List<Integer> channelRestriction) {
        this.channelRestriction = channelRestriction;
    }

    public Boolean getPeriodRestrictionEnabled() {
        return periodRestrictionEnabled;
    }

    public void setPeriodRestrictionEnabled(Boolean periodRestrictionEnabled) {
        this.periodRestrictionEnabled = periodRestrictionEnabled;
    }

    public List<String> getPeriodRestriction() {
        return periodRestriction;
    }

    public void setPeriodRestriction(List<String> periodRestriction) {
        this.periodRestriction = periodRestriction;
    }


    public Boolean getMaxItemRestrictionEnabled() {
        return maxItemRestrictionEnabled;
    }

    public void setMaxItemRestrictionEnabled(Boolean maxItemRestrictionEnabled) {
        this.maxItemRestrictionEnabled = maxItemRestrictionEnabled;
    }

    public Integer getMaxItemRestriction() {
        return maxItemRestriction;
    }

    public void setMaxItemRestriction(Integer maxItemRestriction) {
        this.maxItemRestriction = maxItemRestriction;
    }

    public RatePriceZoneRestrictionDTO getPriceZoneRestriction() {
        return priceZoneRestriction;
    }

    public void setPriceZoneRestriction(RatePriceZoneRestrictionDTO priceZoneRestriction) {
        this.priceZoneRestriction = priceZoneRestriction;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
