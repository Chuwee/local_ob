package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SessionSaleConstraintDTO {

    @JsonProperty("cart_limits_enabled")
    private Boolean cartLimitsEnabled;
    @JsonProperty("cart_limits")
    private CartLimitsDTO cartLimits;
    @JsonProperty("customers_limits_enabled")
    private Boolean customersLimitsEnabled;
    @JsonProperty("customers_limits")
    private CustomersLimitsDTO customersLimits;

    public Boolean getCartLimitsEnabled() {
        return cartLimitsEnabled;
    }

    public void setCartLimitsEnabled(Boolean cartLimitsEnabled) {
        this.cartLimitsEnabled = cartLimitsEnabled;
    }

    public CartLimitsDTO getCartLimits() {
        return cartLimits;
    }

    public void setCartLimits(CartLimitsDTO cartLimits) {
        this.cartLimits = cartLimits;
    }

    public Boolean getCustomersLimitsEnabled() {
        return customersLimitsEnabled;
    }

    public void setCustomersLimitsEnabled(Boolean customersLimitsEnabled) {
        this.customersLimitsEnabled = customersLimitsEnabled;
    }

    public CustomersLimitsDTO getCustomersLimits() {
        return customersLimits;
    }

    public void setCustomersLimits(CustomersLimitsDTO customersLimits) {
        this.customersLimits = customersLimits;
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
