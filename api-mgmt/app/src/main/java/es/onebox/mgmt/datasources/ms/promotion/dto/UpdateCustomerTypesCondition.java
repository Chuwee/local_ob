package es.onebox.mgmt.datasources.ms.promotion.dto;


import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateCustomerTypesCondition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PromotionTargetType type;
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
