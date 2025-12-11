package es.onebox.mgmt.datasources.ms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateOperatorCurrenciesRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("currency_codes")
    List<String> currencyCodes;

    @JsonProperty("default_currency")
    String defaultCurrency;

    public List<String> getCurrencyCodes() {
        return currencyCodes;
    }

    public void setCurrencyCodes(List<String> currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
