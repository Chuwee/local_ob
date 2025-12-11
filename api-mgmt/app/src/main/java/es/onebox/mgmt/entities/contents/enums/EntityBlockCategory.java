package es.onebox.mgmt.entities.contents.enums;

import java.util.stream.Stream;

public enum EntityBlockCategory {
    EMAIL_TEMPLATE("email"),
    PURCHASE_CONFIRM_MODULES("purchase-confirm");

    private final String path;

    EntityBlockCategory(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static EntityBlockCategory fromPath(String path) {
        return Stream.of(values()).filter(v -> v.path.equals(path)).findFirst().orElse(null);
    }
}
