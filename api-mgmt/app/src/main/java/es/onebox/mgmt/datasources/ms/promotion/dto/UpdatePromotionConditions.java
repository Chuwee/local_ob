package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serial;
import java.io.Serializable;

public class UpdatePromotionConditions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UpdateCustomerTypesCondition customerTypesCondition;
    private RatesRelationsCondition ratesRelationsCondition;

    public UpdateCustomerTypesCondition getCustomerTypesCondition() {
        return customerTypesCondition;
    }

    public void setCustomerTypesCondition(UpdateCustomerTypesCondition customerTypesCondition) {
        this.customerTypesCondition = customerTypesCondition;
    }

    public RatesRelationsCondition getRatesRelationsCondition() {
        return ratesRelationsCondition;
    }

    public void setRatesRelationsCondition(RatesRelationsCondition ratesRelationsCondition) {
        this.ratesRelationsCondition = ratesRelationsCondition;
    }
}
