package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSaleConstraintDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("cart_limits_enabled")
    private Boolean cartLimitsEnabled;
    @JsonProperty("cart_limits")
    private UpdateCartLimitsDTO cartLimits;
    @JsonProperty("customers_limits_enabled")
    private Boolean customersLimitsEnabled;
    @JsonProperty("customers_limits")
    private UpdateCustomersLimitsDTO customersLimits;

    public Boolean getCartLimitsEnabled() {
        return cartLimitsEnabled;
    }

    public void setCartLimitsEnabled(Boolean cartLimitsEnabled) {
        this.cartLimitsEnabled = cartLimitsEnabled;
    }

    public UpdateCartLimitsDTO getCartLimits() {
        return cartLimits;
    }

    public void setCartLimits(UpdateCartLimitsDTO cartLimits) {
        this.cartLimits = cartLimits;
    }

    public Boolean getCustomersLimitsEnabled() {
        return customersLimitsEnabled;
    }

    public void setCustomersLimitsEnabled(Boolean customersLimitsEnabled) {
        this.customersLimitsEnabled = customersLimitsEnabled;
    }

    public UpdateCustomersLimitsDTO getCustomersLimits() {
        return customersLimits;
    }

    public void setCustomersLimits(UpdateCustomersLimitsDTO customersLimits) {
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
