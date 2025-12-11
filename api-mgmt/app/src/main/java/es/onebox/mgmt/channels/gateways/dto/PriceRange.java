package es.onebox.mgmt.channels.gateways.dto;

import java.io.Serial;
import java.io.Serializable;

public class PriceRange implements Serializable {

    @Serial
    private static final long serialVersionUID = 2803094729906772242L;

    private Double max;
    private Double min;

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }
}
