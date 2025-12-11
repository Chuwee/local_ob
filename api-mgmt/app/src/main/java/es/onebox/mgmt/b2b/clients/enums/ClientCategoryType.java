package es.onebox.mgmt.b2b.clients.enums;

import java.util.Arrays;

public enum ClientCategoryType {
    AGENCY(1),
    SPONSOR(2),
    PUBLIC_ADMINISTRATION(3),
    COMPANY(4);

    private final Integer id;

    ClientCategoryType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static ClientCategoryType fromId(Integer id) {
        return Arrays.stream(values())
                .filter(elem -> elem.id.equals(id))
                .findFirst()
                .orElse(null);
    }
}
