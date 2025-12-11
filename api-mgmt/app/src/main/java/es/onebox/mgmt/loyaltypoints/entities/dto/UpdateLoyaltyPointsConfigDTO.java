package es.onebox.mgmt.loyaltypoints.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateLoyaltyPointsConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ExpirationDTO expiration;
    @JsonProperty("max_points")
    private MaxPointsDTO maxPoints;
    @Valid
    @JsonProperty("point_exchange")
    private List<PointExchangeDTO> pointExchange;

    public ExpirationDTO getExpiration() { return expiration; }

    public void setExpiration(ExpirationDTO expiration) { this.expiration = expiration; }

    public MaxPointsDTO getMaxPoints() { return maxPoints; }

    public void setMaxPoints(MaxPointsDTO maxPoints) { this.maxPoints = maxPoints; }

    public List<PointExchangeDTO> getPointExchange() { return pointExchange; }

    public void setPointExchange(List<PointExchangeDTO> pointExchange) { this.pointExchange = pointExchange; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
