package es.onebox.mgmt.loyaltypoints.entities.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PointExchangeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Currency code cannot be null")
    @NotEmpty(message = "Currency code cannot be empty")
    private String code;
    @NotNull(message = "Currency value cannot be null")
    @Min(0)
    @Digits(integer = 10, fraction = 2, message = "Currency value can only have up to 2 decimal places")
    private Double value;

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public Double getValue() { return value; }

    public void setValue(Double value) { this.value = value; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
