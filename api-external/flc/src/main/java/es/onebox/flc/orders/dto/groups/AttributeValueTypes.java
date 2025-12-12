package es.onebox.flc.orders.dto.groups;

import java.util.stream.Stream;

public enum AttributeValueTypes {
    USER_INPUT_INTEGER(0),
    USER_INPUT_STRING(1),
    DOMAIN_VALUE(2);

    private Integer id;

    private AttributeValueTypes(int id) {
        this.id = id;
    }

    public static AttributeValueTypes get(int id) {
        return values()[id];
    }

    public int getId() {
        return this.id;
    }

    public static AttributeValueTypes byId(Integer id) {
        return Stream.of(AttributeValueTypes.values())
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }


}
