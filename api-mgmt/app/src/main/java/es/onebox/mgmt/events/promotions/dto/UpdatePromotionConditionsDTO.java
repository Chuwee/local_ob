package es.onebox.mgmt.events.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.RatesRelationsConditionDTO;
import es.onebox.mgmt.common.promotions.dto.UpdateCustomerTypesConditionDTO;

import java.io.Serial;
import java.io.Serializable;

public class UpdatePromotionConditionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("customer_types_condition")
    private UpdateCustomerTypesConditionDTO customerTypesCondition;
    @JsonProperty("rates_relations_condition")
    private RatesRelationsConditionDTO ratesRelationsCondition;


    public UpdateCustomerTypesConditionDTO getCustomerTypesCondition() {
        return customerTypesCondition;
    }

    public void setCustomerTypesCondition(UpdateCustomerTypesConditionDTO customerTypesCondition) {
        this.customerTypesCondition = customerTypesCondition;
    }

    public RatesRelationsConditionDTO getRatesRelationsCondition() {
        return ratesRelationsCondition;
    }

    public void setRatesRelationsCondition(RatesRelationsConditionDTO ratesRelationsCondition) {
        this.ratesRelationsCondition = ratesRelationsCondition;
    }
}
