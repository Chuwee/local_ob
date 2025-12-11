package es.onebox.mgmt.channels.gateways.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serial;
import java.io.Serializable;

public class SurchargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 281355528906772232L;

    @NotNull(message = "Surcharge currency must not be null")
    private String currency;
    @NotNull(message = "Surcharge type must not be null")
    private SurchargeType type;
    @NotNull(message = "Surcharge value must not be null")
    @Positive(message = "Surcharge value must be above 0")
    private Double value;
    @Min(value = 0, message = "Surcharge min_value must be 0 or higher")
    @JsonProperty("min_value")
    private Double minValue;
    @Min(value = 0, message = "Surcharge max_value must be 0 or higher")
    @JsonProperty("max_value")
    private Double maxValue;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public SurchargeType getType() {
        return type;
    }

    public void setType(SurchargeType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
}
