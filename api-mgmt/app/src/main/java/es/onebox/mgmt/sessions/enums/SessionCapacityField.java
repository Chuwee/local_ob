package es.onebox.mgmt.sessions.enums;

import java.util.stream.Stream;

public enum SessionCapacityField {
    ID("row_map.seat_map.id"),
    STATUS("row_map.seat_map.status"),
    BLOCKING_REASON("row_map.seat_map.blocking_reason"),
    SECTOR("row_map.sector"),
    ROW("row_map.name"),
    SEAT("row_map.seat_map.name"),
    NOT_NUMBERED_ZONE("not_numbered_zone_map.name"),
    PRICE_TYPE("row_map.seat_map.price_type"),
    QUOTA("row_map.seat_map.quota"),
    VISIBILITY("row_map.seat_map.visibility"),
    ACCESSIBILITY("row_map.seat_map.accessibility"),
    GATE("row_map.seat_map.gate");

    private final String code;

    SessionCapacityField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static SessionCapacityField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }

}
