package es.onebox.event.datasources.ms.client.dto.conditions;

public enum ConditionGroupType {

    CLIENT_B2B_EVENT(1),
    EVENT(2),
    CLIENT_B2B(3),
    ENTITY(4),
    OPERATOR(5);

    private int groupTypeId;

    ConditionGroupType(int groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public int getGroupTypeId() {
        return groupTypeId;
    }

    public static ConditionGroupType[] getPriorities() {
        return values();
    }
}
