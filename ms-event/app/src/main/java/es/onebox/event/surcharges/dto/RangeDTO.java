package es.onebox.event.surcharges.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class RangeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Double from;
    private Double to;
    private RangeValueDTO values = new RangeValueDTO();
    private Integer currencyId;

    public RangeDTO() {
    }

    public RangeDTO(Double from, Double fixed, Double percentage, Double min, Double max) {
        this.from = from;
        this.values = new RangeValueDTO(fixed, percentage, min, max);
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public RangeValueDTO getValues() {
        return values;
    }

    public void setValues(RangeValueDTO values) {
        this.values = values;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
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
}
