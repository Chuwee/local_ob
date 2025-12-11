package es.onebox.mgmt.datasources.ms.client.enums;

public enum ConditionGroupType {
    CLIENT_B2B_EVENT(1),
    EVENT(2),
    CLIENT_B2B(3),
    ENTITY(4),
    OPERATOR(5);

    private final Integer id;

    ConditionGroupType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
