package es.onebox.mgmt.entities.factory;

import java.io.Serializable;
import java.util.stream.Stream;

public enum InventoryProviderEnum implements Serializable {

    ONEBOX("onebox"),
    SEETICKETS("seetickets"),
    ITALIAN_COMPLIANCE("italian_compliance"),
    SGA("sga");

    private final String code;

    InventoryProviderEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static InventoryProviderEnum getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
