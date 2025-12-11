package es.onebox.mgmt.loyaltypoints.channels.dto;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class MaxLoyaltyPointsPerPurchaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @Min(value = 1, message = "The maximum loyalty points must be a positive natural number")
    private Integer amount;

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
