package es.onebox.mgmt.operators.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OperatorCurrenciesDTO {

    List<OperatorCurrencyDTO> selected;
    @JsonProperty("default_currency")
    String defaultCurrency;


    public List<OperatorCurrencyDTO> getSelected() { return selected; }

    public void setSelected(List<OperatorCurrencyDTO> selected) { this.selected = selected; }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
