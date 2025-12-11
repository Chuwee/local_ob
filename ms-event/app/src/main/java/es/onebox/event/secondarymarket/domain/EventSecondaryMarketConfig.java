package es.onebox.event.secondarymarket.domain;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.event.secondarymarket.dto.SaleType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@CouchDocument
public class EventSecondaryMarketConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<EnabledChannel> enabledChannels;
    private Boolean enabled;
    private ResalePrice price;
    private Commission commission;
    private Boolean isSeasonTicket;
    private Integer numSessions;
    private AdditionalSettings additionalSettings;
    private SaleType saleType;

    private CustomerLimits customerLimits;

    public EventSecondaryMarketConfig() {
    }

    public EventSecondaryMarketConfig(List<EnabledChannel> enabledChannels) {
        this.enabledChannels = enabledChannels;
    }

    public List<EnabledChannel> getEnabledChannels() {
        return enabledChannels;
    }

    public void setEnabledChannels(List<EnabledChannel> enabledChannels) {
        this.enabledChannels = enabledChannels;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ResalePrice getPrice() {
        return price;
    }

    public void setPrice(ResalePrice price) {
        this.price = price;
    }

    public Commission getCommission() {
        return commission;
    }

    public void setCommission(Commission commission) {
        this.commission = commission;
    }

    public Boolean getIsSeasonTicket() {
        return isSeasonTicket;
    }

    public void setIsSeasonTicket(Boolean isSeasonTicket) {
        this.isSeasonTicket = isSeasonTicket;
    }

    public Integer getNumSessions() {
        return numSessions;
    }

    public void setNumSessions(Integer numSessions) {
        this.numSessions = numSessions;
    }

    public AdditionalSettings getAdditionalSettings() {
        return additionalSettings;
    }

    public void setAdditionalSettings(AdditionalSettings additionalSettings) {
        this.additionalSettings = additionalSettings;
    }

    public SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    public CustomerLimits getCustomerLimits() {
        return customerLimits;
    }

    public void setCustomerLimits(CustomerLimits customerLimits) {
        this.customerLimits = customerLimits;
    }
}