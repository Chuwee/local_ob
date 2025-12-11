package es.onebox.mgmt.common.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerTypesConditionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PromotionTargetType type;
    @JsonProperty("customer_types")
    private List<CustomerTypesDTO> customerTypes;

    public PromotionTargetType getType() {
        return type;
    }

    public void setType(PromotionTargetType type) {
        this.type = type;
    }

    public List<CustomerTypesDTO> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(List<CustomerTypesDTO> customerTypes) {
        this.customerTypes = customerTypes;
    }
}
