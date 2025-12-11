package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RatesRelationsCondition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private List<PromoRateCondition> rates;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<PromoRateCondition> getRates() {
        return rates;
    }

    public void setRates(List<PromoRateCondition> rates) {
        this.rates = rates;
    }
}
