package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;

public class EventSurchargeLimit implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double min;
    private Double max;
    private Boolean enabled;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
