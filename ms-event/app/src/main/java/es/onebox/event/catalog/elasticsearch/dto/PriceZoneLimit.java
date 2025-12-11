package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;

public class PriceZoneLimit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1833264727314142935L;

    public PriceZoneLimit() {
    }

    public PriceZoneLimit(Long max, Long min) {
        this.max = max;
        this.min = min;
    }

    private Long max;
    private Long min;

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }
}
