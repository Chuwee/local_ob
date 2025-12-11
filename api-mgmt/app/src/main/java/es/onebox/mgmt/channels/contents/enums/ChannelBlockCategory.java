package es.onebox.mgmt.channels.contents.enums;

import java.util.stream.Stream;

public enum ChannelBlockCategory {
    EMAIL_TEMPLATE("email"),
    LEGAL_TEXTS("legal-texts"),
    DELIVERY_METHODS("delivery-methods"),
    PURCHASE_PROCESS("purchase-process"),
    PRINT_AT_HOME("email-receipt"),
    GENERAL("general"),
    ERROR_TEXTS("error-texts"),
    RECEIPT("receipt"),
    BOOKING("booking"),
    CASHBOX_CLOSING("cashbox"),
    PURCHASE_CONFIRM_MODULES("purchase-confirm-modules");

    private final String path;

    ChannelBlockCategory(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static ChannelBlockCategory fromPath(String path) {
        return Stream.of(values()).filter(v -> v.path.equals(path)).findFirst().orElse(null);
    }
}
