package es.onebox.common.datasources.distribution.dto.order.buyerdata;

public enum GenderField {

    MALE("MALE"),
    FEMALE("FEMALE"),
    NOT_DEFINED("OTHER");

    private final String value;

    GenderField(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static String fromChannelField(String value) {
        return switch (value) {
            case "M" -> MALE.name();
            case "F" -> FEMALE.name();
            case "O" -> NOT_DEFINED.name();
            default -> null;
        };
    }
}