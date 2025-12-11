package es.onebox.mgmt.vouchers.enums;

import java.util.stream.Stream;

public enum VoucherField {
    CODE("code"),
    STATUS("status"),
    PIN("pin"),
    EMAIL("email"),
    BALANCE("balance"),
    EXPIRATION("expiration"),
    USAGES("usages.used"),
    USAGE_LIMIT("usages.limit"),
    CONSOLIDATED_BALANCE("consolidated_balance");

    private String code;

    VoucherField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static VoucherField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
