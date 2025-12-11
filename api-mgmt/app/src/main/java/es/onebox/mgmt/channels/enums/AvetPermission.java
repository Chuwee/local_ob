package es.onebox.mgmt.channels.enums;

import java.util.Arrays;

public enum AvetPermission {
    NO_TICKETS("250"),
    PENDING_APPROVAL("251"),
    LEAVE("252"),
    PENDING_ISSUE("253"),
    ALLOWED_PASSAGE("254"),
    DENIED_PASSAGE("255");

    private final String value;

    AvetPermission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AvetPermission fromValue(String value) {
        return Arrays.stream(values()).filter(it -> it.value.equals(value)).findFirst().orElse(AvetPermission.PENDING_ISSUE);
    }
}
