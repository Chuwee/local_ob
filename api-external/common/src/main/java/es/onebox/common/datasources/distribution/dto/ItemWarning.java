package es.onebox.common.datasources.distribution.dto;

public enum ItemWarning {
    SESSION_NON_CONSECUTIVE_SEAT,
    SESSION_SEVERAL_PRICE_TYPES,
    SESSION_TIERED_PRICES,
    NON_CONFIRMED_DATE,
    SESSION_MIXED_NUMBERED_AND_NON_NUMBERED_SEATS;

    public String value() {
        return name();
    }

    public static ItemWarning fromValue(String v) {
        return valueOf(v);
    }

}
