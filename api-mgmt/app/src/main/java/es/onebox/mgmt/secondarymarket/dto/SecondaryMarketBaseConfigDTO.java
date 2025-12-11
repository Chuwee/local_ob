package es.onebox.mgmt.secondarymarket.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class SecondaryMarketBaseConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @NotNull(message = "price can not be null")
    private ResalePriceDTO price;
    @NotNull(message = "commission can not be null")
    private CommissionDTO commission;
    @JsonProperty("customer_limits_enabled")
    private Boolean customerLimitsEnabled;
    @JsonProperty("customer_limits")
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
}



