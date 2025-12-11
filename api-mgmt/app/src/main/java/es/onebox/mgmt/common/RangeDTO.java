package es.onebox.mgmt.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class RangeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("from")
    private Double from;
    @JsonProperty("to")
    private Double to;
    @JsonProperty("values")
    private RangeValueDTO values = new RangeValueDTO();
    @JsonProperty("currency_code")
    private String currency;

    public RangeDTO() {
    }

    public RangeDTO(Double from, Double fixed, Double percentage, Double min, Double max, String currency) {
        this.from = from;
        this.values = new RangeValueDTO(percentage, min, max, fixed);
        this.currency = currency;
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public RangeValueDTO getValues() {
        return values;
    }

    public void setValues(RangeValueDTO values) {
        this.values = values;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
