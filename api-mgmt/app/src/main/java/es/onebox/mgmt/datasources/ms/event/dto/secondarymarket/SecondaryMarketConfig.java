package es.onebox.mgmt.datasources.ms.event.dto.secondarymarket;

import java.io.Serializable;

public class SecondaryMarketConfig implements Serializable {
    private Boolean enabled;
    private ResalePrice price;
    private Commission commission;
    private Boolean customerLimitsEnabled;
    private CustomerLimits customerLimits;

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

    public CustomerLimits getCustomerLimits() {
        return customerLimits;
    }

    public void setCustomerLimits(
        CustomerLimits customerLimits) {
        this.customerLimits = customerLimits;
    }

    public Boolean getCustomerLimitsEnabled() {
        return customerLimitsEnabled;
    }

    public void setCustomerLimitsEnabled(Boolean customerLimitsEnabled) {
        this.customerLimitsEnabled = customerLimitsEnabled;
    }
}