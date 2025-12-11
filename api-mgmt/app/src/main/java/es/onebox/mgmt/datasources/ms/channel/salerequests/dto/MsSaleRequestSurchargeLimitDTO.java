package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import java.io.Serializable;

public class MsSaleRequestSurchargeLimitDTO implements Serializable {

    private static final long serialVersionUID = 4396612286450830150L;

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
