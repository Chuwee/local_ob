package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class LoyaltyPointsConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Expiration expiration;
    private MaxPoints maxPoints;
    private List<PointExchange> pointExchange;
    private ZonedDateTime lastReset;

    public Expiration getExpiration() { return expiration; }

    public void setExpiration(Expiration expiration) { this.expiration = expiration; }

    public MaxPoints getMaxPoints() { return maxPoints; }

    public void setMaxPoints(MaxPoints maxPoints) { this.maxPoints = maxPoints; }

    public List<PointExchange> getPointExchange() { return pointExchange; }

    public void setPointExchange(List<PointExchange> pointExchange) { this.pointExchange = pointExchange; }

    public ZonedDateTime getLastReset() { return lastReset; }

    public void setLastReset(ZonedDateTime lastReset) { this.lastReset = lastReset; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
