package es.onebox.event.secondarymarket.dto;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SecondaryMarketConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @NotNull
    private ResalePriceDTO price;
    @NotNull
    private CommissionDTO commission;
    private DatesDTO dates;
    private Boolean isSeasonTicket;
    private Integer numSessions;
    private AdditionalSettingsDTO additionalSettings;
    private SaleType saleType;
    private Boolean customerLimitsEnabled;
    private CustomerLimitsDTO customerLimits;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ResalePriceDTO getPrice() {
        return price;
    }

    public void setPrice(ResalePriceDTO price) {
        this.price = price;
    }

    public CommissionDTO getCommission() {
        return commission;
    }

    public void setCommission(CommissionDTO commission) {
        this.commission = commission;
    }

    public DatesDTO getDates() {
        return dates;
    }

    public void setDates(DatesDTO dates) {
        this.dates = dates;
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

    public AdditionalSettingsDTO getAdditionalSettings() {
        return additionalSettings;
    }

    public void setAdditionalSettings(AdditionalSettingsDTO additionalSettings) {
        this.additionalSettings = additionalSettings;
    }

    public SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    public CustomerLimitsDTO getCustomerLimits() {
        return customerLimits;
    }

    public void setCustomerLimits(CustomerLimitsDTO customerLimits) {
        this.customerLimits = customerLimits;
    }

    public Boolean getCustomerLimitsEnabled() {
        return customerLimitsEnabled;
    }

    public void setCustomerLimitsEnabled(Boolean customerLimitsEnabled) {
        this.customerLimitsEnabled = customerLimitsEnabled;
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
