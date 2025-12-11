package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class CustomersLimits implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<Long, PriceZoneLimit> priceZoneLimit;

    private Long min;

    private Long max;

    public Map<Long, PriceZoneLimit> getPriceZoneLimit() {
        return priceZoneLimit;
    }

    public void setPriceZoneLimit(Map<Long, PriceZoneLimit> priceZoneLimit) {
        this.priceZoneLimit = priceZoneLimit;
    }

    public Long getMin() { return min; }

    public void setMin(Long min) { this.min = min; }

    public Long getMax() { return max; }

    public void setMax(Long max) { this.max = max; }
}
