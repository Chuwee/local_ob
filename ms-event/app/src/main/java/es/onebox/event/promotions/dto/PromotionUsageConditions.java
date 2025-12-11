package es.onebox.event.promotions.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PromotionUsageConditions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1170427026998326933L;

    private List<RatesRelationsCondition> ratesRelationsConditions;

    public List<RatesRelationsCondition> getRatesRelationsConditions() {
        return ratesRelationsConditions;
    }

    public void setRatesRelationsConditions(List<RatesRelationsCondition> ratesRelationsConditions) {
        this.ratesRelationsConditions = ratesRelationsConditions;
    }
}
