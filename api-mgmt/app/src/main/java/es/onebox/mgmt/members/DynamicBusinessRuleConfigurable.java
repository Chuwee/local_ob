package es.onebox.mgmt.members;

public interface DynamicBusinessRuleConfigurable {

    MemberOrderType getOrderType();

    String getOperationName();
    String getId();
    String getJavaClass();
    DynamicBusinessRuleFields[] getFields();

}
