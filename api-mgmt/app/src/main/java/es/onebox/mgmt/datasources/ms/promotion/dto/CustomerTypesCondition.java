package es.onebox.mgmt.datasources.ms.promotion.dto;

import es.onebox.mgmt.common.promotions.dto.CustomerTypesDTO;
import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;

import java.util.List;

public class CustomerTypesCondition {

    private PromotionTargetType type;
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
