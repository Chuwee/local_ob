package es.onebox.mgmt.common.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RatesRelationsConditionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @JsonProperty("rates")
    private List<PromoRateConditionDTO> rates;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<PromoRateConditionDTO> getRates() {
        return rates;
    }

    public void setRates(List<PromoRateConditionDTO> rates) {
        this.rates = rates;
    }
}
