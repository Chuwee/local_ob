package es.onebox.mgmt.datasources.ms.promotion.dto;

public class PromotionConditions {
    private CustomerTypesCondition customerTypesCondition;
    private RatesRelationsCondition ratesRelationsCondition;

    public CustomerTypesCondition getCustomerTypesCondition() {
        return customerTypesCondition;
    }

    public void setCustomerTypesCondition(CustomerTypesCondition customerTypesCondition) {
        this.customerTypesCondition = customerTypesCondition;
    }

    public RatesRelationsCondition getRatesRelationsCondition() {
        return ratesRelationsCondition;
    }

    public void setRatesRelationsCondition(RatesRelationsCondition ratesRelationsCondition) {
        this.ratesRelationsCondition = ratesRelationsCondition;
    }
}
