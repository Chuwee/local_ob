package es.onebox.event.datasources.ms.client.dto.conditions;


import es.onebox.event.datasources.ms.client.dto.conditions.generic.MultiValueCondition;

public class PaymentMethodsCondition extends MultiValueCondition<String> {
    public static final Integer type;

    public PaymentMethodsCondition() {
    }

    static {
        type = ConditionType.PAYMENT_METHODS.getType();
    }
}