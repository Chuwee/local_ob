package es.onebox.mgmt.datasources.ms.channel.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelLoyaltyPoints  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean allowLoyaltyPoints;
    private MaxLoyaltyPointsPerPurchase maxLoyaltyPointsPerPurchase;
    private LoyaltyPointsPercentagePerPurchase loyaltyPointsPercentagePerPurchase;

    public Boolean getAllowLoyaltyPoints() { return allowLoyaltyPoints; }

    public void setAllowLoyaltyPoints(Boolean allowLoyaltyPoints) { this.allowLoyaltyPoints = allowLoyaltyPoints; }

    public MaxLoyaltyPointsPerPurchase getMaxLoyaltyPointsPerPurchase() { return maxLoyaltyPointsPerPurchase; }

    public void setMaxLoyaltyPointsPerPurchase(MaxLoyaltyPointsPerPurchase maxLoyaltyPointsPerPurchase) {
        this.maxLoyaltyPointsPerPurchase = maxLoyaltyPointsPerPurchase;
    }

    public LoyaltyPointsPercentagePerPurchase getLoyaltyPointsPercentagePerPurchase() { return loyaltyPointsPercentagePerPurchase; }

    public void setLoyaltyPointsPercentagePerPurchase(LoyaltyPointsPercentagePerPurchase loyaltyPointsPercentagePerPurchase) {
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
