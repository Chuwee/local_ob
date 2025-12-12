package es.onebox.common.datasources.ms.order.dto;

public enum AttributeValueTypes {
    USER_INPUT_INTEGER(0),
    USER_INPUT_STRING(1),
    DOMAIN_VALUE(2);

    private int id;

    private AttributeValueTypes(int id) {
        this.id = id;
    }

    public static AttributeValueTypes get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }
}
