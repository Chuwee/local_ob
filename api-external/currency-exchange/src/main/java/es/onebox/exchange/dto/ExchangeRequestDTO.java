package es.onebox.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ExchangeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("currency_code")
    private String currencyCode;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
