package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateSaleConstraintDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean cartLimitsEnabled;
    private Integer cartLimit;
    private Boolean cartPriceTypeLimitsEnabled;
    private List<UpdatePriceTypeLimitDTO> cartPriceTypeLimits;
    private Boolean customersLimitsEnabled;
    private UpdateCustomersLimitsDTO customersLimits;

    public Boolean getCartLimitsEnabled() {
        return cartLimitsEnabled;
    }

    public void setCartLimitsEnabled(Boolean cartLimitsEnabled) {
        this.cartLimitsEnabled = cartLimitsEnabled;
    }

    public Integer getCartLimit() {
        return cartLimit;
    }

    public void setCartLimit(Integer cartLimit) {
        this.cartLimit = cartLimit;
    }

    public Boolean getCartPriceTypeLimitsEnabled() {
        return cartPriceTypeLimitsEnabled;
    }

    public void setCartPriceTypeLimitsEnabled(Boolean cartPriceTypeLimitsEnabled) {
        this.cartPriceTypeLimitsEnabled = cartPriceTypeLimitsEnabled;
    }

    public List<UpdatePriceTypeLimitDTO> getCartPriceTypeLimits() {
        return cartPriceTypeLimits;
    }

    public void setCartPriceTypeLimits(List<UpdatePriceTypeLimitDTO> cartPriceTypeLimits) {
        this.cartPriceTypeLimits = cartPriceTypeLimits;
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
