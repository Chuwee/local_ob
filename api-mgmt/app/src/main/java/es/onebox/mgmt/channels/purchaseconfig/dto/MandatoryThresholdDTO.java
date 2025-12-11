package es.onebox.mgmt.channels.purchaseconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.io.Serializable;

public class MandatoryThresholdDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5233151419355350087L;

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("amount")
    @Min(value = 0, message = "Threshold amount cannot be negative")
    private Double amount;

    public MandatoryThresholdDTO() {}

    public MandatoryThresholdDTO(String currency, Double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
