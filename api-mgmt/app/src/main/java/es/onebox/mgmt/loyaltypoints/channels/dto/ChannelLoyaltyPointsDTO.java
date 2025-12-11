package es.onebox.mgmt.loyaltypoints.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelLoyaltyPointsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("allow_loyalty_points")
    private Boolean allowLoyaltyPoints;
    @JsonProperty("max_loyalty_points_per_purchase")
    @Valid
    private MaxLoyaltyPointsPerPurchaseDTO maxLoyaltyPointsPerPurchase;
    @JsonProperty("loyalty_points_percentage_per_purchase")
    @Valid
    private LoyaltyPointsPercentagePerPurchaseDTO loyaltyPointsPercentagePerPurchase;

    public Boolean getAllowLoyaltyPoints() { return allowLoyaltyPoints; }

    public void setAllowLoyaltyPoints(Boolean allowLoyaltyPoints) { this.allowLoyaltyPoints = allowLoyaltyPoints; }

    public MaxLoyaltyPointsPerPurchaseDTO getMaxLoyaltyPointsPerPurchase() { return maxLoyaltyPointsPerPurchase; }

    public void setMaxLoyaltyPointsPerPurchase(MaxLoyaltyPointsPerPurchaseDTO maxLoyaltyPointsPerPurchase) {
        this.maxLoyaltyPointsPerPurchase = maxLoyaltyPointsPerPurchase;
    }

    public LoyaltyPointsPercentagePerPurchaseDTO getLoyaltyPointsPercentagePerPurchase() { return loyaltyPointsPercentagePerPurchase; }

    public void setLoyaltyPointsPercentagePerPurchase(LoyaltyPointsPercentagePerPurchaseDTO loyaltyPointsPercentagePerPurchase) {
        this.loyaltyPointsPercentagePerPurchase = loyaltyPointsPercentagePerPurchase;
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
