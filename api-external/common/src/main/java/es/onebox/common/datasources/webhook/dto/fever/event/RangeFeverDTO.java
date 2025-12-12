package es.onebox.common.datasources.webhook.dto.fever.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serial;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class RangeFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Double from;
    private Double to;
    private RangeValueFeverDTO values = new RangeValueFeverDTO();
    private Integer currencyId;

    public RangeFeverDTO() {
    }

    public RangeFeverDTO(Double from, Double fixed, Double percentage, Double min, Double max) {
        this.from = from;
        this.values = new RangeValueFeverDTO(fixed, percentage, min, max);
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public RangeValueFeverDTO getValues() {
        return values;
    }

    public void setValues(RangeValueFeverDTO values) {
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
