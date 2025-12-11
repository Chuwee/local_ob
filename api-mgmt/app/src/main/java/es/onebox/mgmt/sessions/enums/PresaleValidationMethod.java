package es.onebox.mgmt.sessions.enums;

import java.util.Arrays;

public enum PresaleValidationMethod {

    PROMOTIONAL_CODE,
    USER,
    USER_PASSWORD,
    USER_CODE_PASSWORD;

    public static PresaleValidationMethod getByName(final String name) {
        return Arrays.stream(values()).filter(field -> field.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
