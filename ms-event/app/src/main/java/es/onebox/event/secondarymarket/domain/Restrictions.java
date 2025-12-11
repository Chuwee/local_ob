package es.onebox.event.secondarymarket.domain;

import java.io.Serial;
import java.io.Serializable;

public class Restrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double min;
    private Double max;

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
}
