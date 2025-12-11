package es.onebox.mgmt.common.conditions.conditiontype;

public class PaymentMethodsCondition extends McCondition<String> {
    public static final Integer type;

    public PaymentMethodsCondition() {
    }

    static {
        type = ConditionType.PAYMENT_METHODS.getType();
    }
}
