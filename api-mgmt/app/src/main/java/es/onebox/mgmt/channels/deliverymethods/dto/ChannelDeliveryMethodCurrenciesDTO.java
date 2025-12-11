package es.onebox.mgmt.channels.deliverymethods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelDeliveryMethodCurrenciesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(value = 0, message = "cost must be equal or above 0")
    private Double cost;
    @JsonProperty("currency_code")
    private String currencyCode;

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCurrencyCode() { return currencyCode;  }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
