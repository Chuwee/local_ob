package es.onebox.mgmt.sessions.enums;

import java.util.stream.Stream;

public enum WhiteListField {
    BARCODE("barcode"),
    STATUS("status"),
    EVENT_ID("event.id"),
    EVENT_NAME("event.name"),
    SESSION_ID("session.id"),
    SESSION_NAME("session.name"),
    SESSION_START_DATE("session.start_date"),
    VIEW("view"),
    ROW("row"),
    SEAT("seat"),
    GATE("gate"),
    SECTOR("sector"),
    PRICE_TYPE("price_type"),
    RELATED_SESSION_ID("related_session.id");

    private final String code;

    WhiteListField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static WhiteListField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
