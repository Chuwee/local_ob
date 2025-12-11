package es.onebox.event.datasources.ms.client.dto.conditions.generic;

public class ConditionCurrencyValue {

    private Integer currencyId;
    private Double value;

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
