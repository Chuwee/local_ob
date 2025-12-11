package es.onebox.mgmt.loyaltypoints.channels.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class LoyaltyPointsPercentagePerPurchaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @Min(value = 1, message = "The percentage must be between 1 and 100")
    @Max(value = 100, message = "The percentage must be between 1 and 100")
    private Double percentage;

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Double getPercentage() { return percentage; }

    public void setPercentage(Double percentage) { this.percentage = percentage; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
