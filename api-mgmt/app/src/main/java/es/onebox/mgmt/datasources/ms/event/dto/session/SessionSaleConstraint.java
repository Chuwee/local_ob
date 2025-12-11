package es.onebox.mgmt.datasources.ms.event.dto.session;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionSaleConstraint implements Serializable {

    private Boolean cartLimitsEnabled;
    private Integer cartLimit;
    private Boolean cartPriceTypeLimitsEnabled;
    private List<PriceTypeLimit> cartPriceTypeLimits;
    private Boolean customersLimitsEnabled;
    private CustomersLimits customersLimits;

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

    public List<PriceTypeLimit> getCartPriceTypeLimits() {
        return cartPriceTypeLimits;
    }

    public void setCartPriceTypeLimits(List<PriceTypeLimit> cartPriceTypeLimits) {
        this.cartPriceTypeLimits = cartPriceTypeLimits;
    }

    public Boolean getCustomersLimitsEnabled() {
        return customersLimitsEnabled;
    }

    public void setCustomersLimitsEnabled(Boolean customersLimitsEnabled) {
        this.customersLimitsEnabled = customersLimitsEnabled;
    }

    public CustomersLimits getCustomersLimits() {
        return customersLimits;
    }

    public void setCustomersLimits(CustomersLimits customersLimits) {
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
