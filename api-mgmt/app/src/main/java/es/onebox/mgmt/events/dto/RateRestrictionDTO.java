package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class RateRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("date_restriction_enabled")
    private Boolean dateRestrictionEnabled;

    @JsonProperty("date_restriction")
    private EventRateDateRestrictionDTO dateRestriction;

    @JsonProperty("customer_type_restriction_enabled")
    private Boolean customerTypeRestrictionEnabled;

    @JsonProperty("customer_type_restriction")
    private List<Long> customerTypeRestriction;

    @JsonProperty("rate_relations_restriction")
    private RateRelationsRestrictionDTO rateRelationsRestriction;

    @JsonProperty("rate_relations_restriction_enabled")
    private Boolean rateRelationsRestrictionEnabled;

    @JsonProperty("price_type_restriction_enabled")
    private Boolean priceTypeRestrictionEnabled;

    @JsonProperty("price_type_restriction")
    private RatePriceTypeRestrictionDTO priceTypeRestriction;

    @JsonProperty("channel_restriction_enabled")
    private Boolean channelRestrictionEnabled;

    @JsonProperty("channel_restriction")
    private List<Integer> channelRestriction;

    @JsonProperty("period_restriction_enabled")
    private Boolean periodRestrictionEnabled;

    @JsonProperty("period_restriction")
    private List<String> periodRestriction;

    @JsonProperty("max_item_restriction_enabled")
    private Boolean maxItemRestrictionEnabled;

    @JsonProperty("max_item_restriction")
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

    public Boolean getPriceTypeRestrictionEnabled() {
        return priceTypeRestrictionEnabled;
    }

    public void setPriceTypeRestrictionEnabled(Boolean priceTypeRestrictionEnabled) {
        this.priceTypeRestrictionEnabled = priceTypeRestrictionEnabled;
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

    public RatePriceTypeRestrictionDTO getPriceTypeRestriction() {
        return priceTypeRestriction;
    }

    public void setPriceTypeRestriction(RatePriceTypeRestrictionDTO priceTypeRestriction) {
        this.priceTypeRestriction = priceTypeRestriction;
    }
}
