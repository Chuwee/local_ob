package es.onebox.mgmt.members;

import static es.onebox.mgmt.members.DynamicBusinessRuleFields.MEMBER_ROLE_RELATIONS;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.ROLE_AGE_RELATIONS;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.SUBSCRIPTION_MODES;
import static es.onebox.mgmt.members.MemberOrderType.BUY_SEAT;

public enum DynamicMemberInferer implements DynamicBusinessRuleConfigurable {
    SUBSCRIPTION_MODE_PERIODICITY_INFERER(null, "SUBSCRIPTION_MODE_INFERER", "subscriptionModeInferData","SubscriptionModePeriodicityInferer", SUBSCRIPTION_MODES),
    PARTNER_ROLES_INFERER(BUY_SEAT,"ROLE_INFERER", "partnerRolesRelationInferData", "NewSeatPartnerRolesRelationInferer", MEMBER_ROLE_RELATIONS),
    PARTNER_ROLES_AGE_INFERER(BUY_SEAT, "ROLE_INFERER", "roleInferData", "NewSeatRolesAgeInferer", ROLE_AGE_RELATIONS);

    private final MemberOrderType orderType;
    private final String operationName;
    private final String id;
    private final String javaClass;
    private final DynamicBusinessRuleFields[] fields;

    DynamicMemberInferer(MemberOrderType orderType, String operationName, String id, String javaClass, DynamicBusinessRuleFields... fields) {
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
