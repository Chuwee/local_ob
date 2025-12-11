package es.onebox.mgmt.datasources.ms.client.dto;


import es.onebox.mgmt.common.conditions.Condition;
import es.onebox.mgmt.common.conditions.conditiontype.ConditionCurrencyValue;

import java.util.List;

public class ConditionData<T> extends Condition {


    protected List<ConditionCurrencyValue> currencies;

    public List<ConditionCurrencyValue> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<ConditionCurrencyValue> currencies) {
        this.currencies = currencies;
    }
}
