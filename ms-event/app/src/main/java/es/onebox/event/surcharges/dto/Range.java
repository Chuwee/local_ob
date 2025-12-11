package es.onebox.event.surcharges.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Range implements Serializable, Comparable<Range> {
    private static final long serialVersionUID = 1L;

    private Double from;
    private Double to;
    private Double percentage;
    private Double min;
    private Double max;
    private Double fixed;
    private Integer currencyId;

    public Range() {
    }

    public Range(Double from, Double fixed, Double percentage, Double min, Double max, Integer currencyId) {
        this.from = from;
        this.percentage = percentage;
        this.min = min;
        this.max = max;
        this.fixed = fixed;
        this.currencyId = currencyId;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
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

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public int compareTo(Range o) {

        if (this.from == null || o.getFrom() == null) {
            return 0;
        }

        if (this.from > o.from) {
            return 1;
        } else if (this.from < o.from) {
            return -1;
        }

        return 0;
    }
}
