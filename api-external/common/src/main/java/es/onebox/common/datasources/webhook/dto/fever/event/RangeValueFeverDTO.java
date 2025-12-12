package es.onebox.common.datasources.webhook.dto.fever.event;

import java.io.Serializable;

public class RangeValueFeverDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double percentage;
    private Double min;
    private Double max;
    private Double fixed;

    public RangeValueFeverDTO() {
    }

    public RangeValueFeverDTO(Double percentage, Double min, Double max, Double fixed) {
        this.percentage = percentage;
        this.min = min;
        this.max = max;
        this.fixed = fixed;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

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

    public Double getFixed() {
        return fixed;
    }

    public void setFixed(Double fixed) {
        this.fixed = fixed;
    }
}
