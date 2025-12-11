package es.onebox.mgmt.vouchers.dto;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PriceRangeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Min(value = 0, message = "from must be above 0")
    private Integer from;
    @Min(value = 0, message = "to must be above 0")
    private Integer to;

    public PriceRangeDTO() {
    }

    public PriceRangeDTO(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
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
