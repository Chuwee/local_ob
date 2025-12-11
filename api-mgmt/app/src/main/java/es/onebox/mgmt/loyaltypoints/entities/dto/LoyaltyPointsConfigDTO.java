package es.onebox.mgmt.loyaltypoints.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class LoyaltyPointsConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ExpirationDTO expiration;
    @JsonProperty("max_points")
    private MaxPointsDTO maxPoints;
    @JsonProperty("point_exchange")
    private List<PointExchangeDTO> pointExchange;
    @JsonProperty("last_reset")
    private ZonedDateTime lastReset;

    public ExpirationDTO getExpiration() { return expiration; }

    public void setExpiration(ExpirationDTO expiration) { this.expiration = expiration; }

    public MaxPointsDTO getMaxPoints() { return maxPoints; }

    public void setMaxPoints(MaxPointsDTO maxPoints) { this.maxPoints = maxPoints; }

    public List<PointExchangeDTO> getPointExchange() { return pointExchange; }

    public void setPointExchange(List<PointExchangeDTO> pointExchange) { this.pointExchange = pointExchange; }

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
