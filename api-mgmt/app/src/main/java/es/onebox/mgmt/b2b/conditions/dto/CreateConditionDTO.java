package es.onebox.mgmt.b2b.conditions.dto;


import es.onebox.mgmt.common.conditions.Condition;

import java.util.List;

public class CreateConditionDTO<T> extends Condition {

    protected List<ConditionCurrenciesDTO> currencies;

    public List<ConditionCurrenciesDTO> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<ConditionCurrenciesDTO> currencies) {
        this.currencies = currencies;
    }
}
