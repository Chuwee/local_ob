package es.onebox.mgmt.members;

import static es.onebox.mgmt.members.DynamicBusinessRuleFields.ALLOWED_CAPACITIES;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.ALLOWED_ROLES;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.COMMENT_CONTENT;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.COMMENT_ID;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.EXCLUDED_PERIODICITIES;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.MEMBER_ROLE_RELATIONS;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.PAYED_TERMS_EXCLUSIONS;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.PERMISSION_BLACK_LIST;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.PERMISSION_WHITE_LIST;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.UNPAYED_TERMS_EXCLUSIONS;
import static es.onebox.mgmt.members.MemberOrderType.BUY_SEAT;
import static es.onebox.mgmt.members.MemberOrderType.CHANGE_SEAT;
import static es.onebox.mgmt.members.MemberOrderType.RENEWAL;

public enum DynamicMemberValidations implements DynamicBusinessRuleConfigurable {
    CAPACITY_PERIODICITY_VALIDATOR(CHANGE_SEAT, "CHANGE_SEAT_VALIDATOR", "changeSeatValidatorData", "ChangeSeatCapacityPeriodicityValidator", ALLOWED_CAPACITIES, ALLOWED_ROLES, EXCLUDED_PERIODICITIES, COMMENT_ID, COMMENT_CONTENT, PERMISSION_WHITE_LIST, PERMISSION_BLACK_LIST),
    ROLE_TERMS_VALIDATION(BUY_SEAT, "BUY_SEAT_VALIDATOR", "buySeatValidatorData","BuySeatRolePermissionValidator", MEMBER_ROLE_RELATIONS, PAYED_TERMS_EXCLUSIONS, UNPAYED_TERMS_EXCLUSIONS, PERMISSION_WHITE_LIST, PERMISSION_BLACK_LIST),
    PERMISSION_VALIDATOR(RENEWAL, "RENEWAL_VALIDATOR", "renewalValidatorData", "RenewalPermissionsValidator", PERMISSION_WHITE_LIST, PERMISSION_BLACK_LIST),
    PAYMENTS_PERMISSIONS_VALIDATOR(RENEWAL, "RENEWAL_VALIDATOR","renewalValidatorData", "RenewalMultiplePaymentsPermissionsValidator", PERMISSION_WHITE_LIST, PERMISSION_BLACK_LIST);

    private MemberOrderType orderType;
    private String operationName;
    private String id;
    private String javaClass;
    private DynamicBusinessRuleFields[] fields;

    DynamicMemberValidations(MemberOrderType orderType, String operationName, String id, String javaClass, DynamicBusinessRuleFields... fields) {
        this.orderType = orderType;
        this.operationName = operationName;
        this.id = id;
        this.javaClass = javaClass;
        this.fields = fields;
    }

    public MemberOrderType getOrderType() {
        return orderType;
    }

    public String getId() {
        return id;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public DynamicBusinessRuleFields[] getFields() {
        return fields;
    }

}
