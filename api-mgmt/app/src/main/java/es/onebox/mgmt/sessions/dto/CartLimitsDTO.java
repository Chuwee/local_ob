package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CartLimitsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7501130428738268318L;

    private Integer limit;
    @JsonProperty("price_type_limits_enabled")
    private Boolean priceTypeLimitsEnabled;
    @JsonProperty("price_type_limits")
    private List<PriceTypeLimitDTO> priceTypeLimits;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Boolean getPriceTypeLimitsEnabled() {
        return priceTypeLimitsEnabled;
    }

    public void setPriceTypeLimitsEnabled(Boolean priceTypeLimitsEnabled) {
        this.priceTypeLimitsEnabled = priceTypeLimitsEnabled;
    }

    public List<PriceTypeLimitDTO> getPriceTypeLimits() {
        return priceTypeLimits;
    }

    public void setPriceTypeLimits(List<PriceTypeLimitDTO> priceTypeLimits) {
        this.priceTypeLimits = priceTypeLimits;
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
