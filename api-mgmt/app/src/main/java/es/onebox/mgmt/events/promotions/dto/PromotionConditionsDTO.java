package es.onebox.mgmt.events.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.CustomerTypesConditionDTO;
import es.onebox.mgmt.common.promotions.dto.RatesRelationsConditionDTO;

import java.io.Serial;
import java.io.Serializable;

public class PromotionConditionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("customer_types_condition")
    private CustomerTypesConditionDTO customerTypesCondition;
    @JsonProperty("rates_relations_condition")
    private RatesRelationsConditionDTO ratesRelationsCondition;

    public CustomerTypesConditionDTO getCustomerTypesCondition() {
        return customerTypesCondition;
    }

    public void setCustomerTypesCondition(CustomerTypesConditionDTO customerTypesCondition) {
        this.customerTypesCondition = customerTypesCondition;
    }

    public RatesRelationsConditionDTO getRatesRelationsCondition() {
        return ratesRelationsCondition;
    }

    public void setRatesRelationsCondition(RatesRelationsConditionDTO ratesRelationsCondition) {
        this.ratesRelationsCondition = ratesRelationsCondition;
    }
}
