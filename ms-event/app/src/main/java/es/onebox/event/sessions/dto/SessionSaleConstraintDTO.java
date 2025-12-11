package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionSaleConstraintDTO implements Serializable {

    private Integer sessionId;
    private Integer cartLimit;
    private List<PriceTypeLimitDTO> cartPriceTypeLimits;
    private CustomersLimitsDTO customersLimits;

    public Integer getCartLimit() {
        return cartLimit;
    }

    public void setCartLimit(Integer cartLimit) {
        this.cartLimit = cartLimit;
    }

    public List<PriceTypeLimitDTO> getCartPriceTypeLimits() {
        return cartPriceTypeLimits;
    }

    public void setCartPriceTypeLimits(List<PriceTypeLimitDTO> cartPriceTypeLimits) {
        this.cartPriceTypeLimits = cartPriceTypeLimits;
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

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }
}
