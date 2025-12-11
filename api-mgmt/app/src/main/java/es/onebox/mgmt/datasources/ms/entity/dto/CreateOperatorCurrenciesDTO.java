package es.onebox.mgmt.datasources.ms.entity.dto;

import java.util.List;

public class CreateOperatorCurrenciesDTO {

    List<String> currencyCodes;

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
