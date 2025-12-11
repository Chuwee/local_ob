package es.onebox.event.sessions.domain.sessionconfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomersLimits implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<PriceTypeLimit> priceTypeLimits;

    private Integer min;

    private Integer max;

    public List<PriceTypeLimit> getPriceTypeLimits() {
        return priceTypeLimits;
    }

    public void setPriceTypeLimits(List<PriceTypeLimit> priceTypeLimits) {
        this.priceTypeLimits = priceTypeLimits;
    }

    public Integer getMin() { return min; }

    public void setMin(Integer min) { this.min = min; }

    public Integer getMax() { return max; }

    public void setMax(Integer max) { this.max = max; }
}
