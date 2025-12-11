package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateCustomersLimitsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<UpdatePriceTypeLimitDTO> priceTypeLimits;

    private Integer min;

    private Integer max;

    public List<UpdatePriceTypeLimitDTO> getPriceTypeLimits() {
        return priceTypeLimits;
    }

    public void setPriceTypeLimits(List<UpdatePriceTypeLimitDTO> priceTypeLimits) {
        this.priceTypeLimits = priceTypeLimits;
    }

    public Integer getMin() { return min; }

    public void setMin(Integer min) { this.min = min; }

    public Integer getMax() { return max; }

    public void setMax(Integer max) { this.max = max; }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
