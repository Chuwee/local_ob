package es.onebox.mgmt.collectives.collectivecodes.enums;

import java.util.stream.Stream;

public enum CollectiveCodeField {
    CODE("code"),
    KEY("key"),
    VALIDATION_METHOD("validation_method"),
    VALIDITY_PERIOD_FROM("validity_period.from"),
    VALIDITY_PERIOD_TO("validity_period.to"),
    USAGE_LIMIT("usage.limit"),
    USAGE_CURRENT("usage.current");

    private String code;

    CollectiveCodeField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static CollectiveCodeField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
