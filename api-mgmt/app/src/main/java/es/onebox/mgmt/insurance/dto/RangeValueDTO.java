package es.onebox.mgmt.insurance.dto;

import java.io.Serializable;

public class RangeValueDTO implements Serializable {
    private static final long serialVersionUID = -2661252015114676699L;

    private Double percentage;
    private Double min;
    private Double max;
    private Double fixed;

    public RangeValueDTO() {
    }

    public RangeValueDTO(Double percentage, Double min, Double max, Double fixed) {
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
