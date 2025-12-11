package es.onebox.mgmt.members;

import static es.onebox.mgmt.members.DynamicBusinessRuleFields.DISCOUNT_RANGE;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.SENIORITY_THRESHOLD;
import static es.onebox.mgmt.members.MemberOrderType.CHANGE_SEAT;

public enum DynamicMemberDiscountsCalculators implements DynamicBusinessRuleConfigurable {

    PARTNER_SENIORITY_CALCULATOR(CHANGE_SEAT, "DISCOUNT_CALCULATOR", "discountCalculatorData", "PartnerSeniorityDiscountCalculator", DISCOUNT_RANGE, SENIORITY_THRESHOLD);

    private final MemberOrderType orderType;
    private final String operationName;
    private final String id;
    private final String javaClass;
    private final DynamicBusinessRuleFields[] fields;

    DynamicMemberDiscountsCalculators(MemberOrderType orderType, String operationName, String id, String javaClass, DynamicBusinessRuleFields... fields) {
        this.orderType = orderType;
        this.operationName = operationName;
        this.id = id;
        this.javaClass = javaClass;
        this.fields = fields;
    }

    public MemberOrderType getOrderType() {
        return orderType;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getId() {
        return id;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public DynamicBusinessRuleFields[] getFields() {
        return fields;
    }

}
