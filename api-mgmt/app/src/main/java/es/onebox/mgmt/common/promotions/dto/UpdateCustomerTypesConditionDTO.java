package es.onebox.mgmt.common.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateCustomerTypesConditionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PromotionTargetType type;
    @JsonProperty("customer_types")
    private List<Long> customerTypeIds;

    public PromotionTargetType getType() {
        return type;
    }

    public void setType(PromotionTargetType type) {
        this.type = type;
    }

    public List<Long> getCustomerTypeIds() {
        return customerTypeIds;
    }

    public void setCustomerTypeIds(List<Long> customerTypeIds) {
        this.customerTypeIds = customerTypeIds;
    }
}
