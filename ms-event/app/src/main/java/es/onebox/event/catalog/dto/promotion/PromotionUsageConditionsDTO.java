package es.onebox.event.catalog.dto.promotion;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PromotionUsageConditionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 308438984266250777L;

    private List<RatesRelationsConditionDTO> ratesRelationsConditions;

    public List<RatesRelationsConditionDTO> getRatesRelationsConditions() {
        return ratesRelationsConditions;
    }

    public void setRatesRelationsConditions(List<RatesRelationsConditionDTO> ratesRelationsConditions) {
        this.ratesRelationsConditions = ratesRelationsConditions;
    }
}
