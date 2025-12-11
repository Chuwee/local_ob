package es.onebox.mgmt.sessions.enums;

import java.util.stream.Stream;

public enum ExternalBarcodesField {
    BARCODE("barcode"),
    STATUS("status"),
    EVENT_ID("event.id"),
    EVENT_NAME("event.name"),
    SESSION_ID("session.id"),
    SESSION_NAME("session.name"),
    SESSION_START_DATE("session.start_date"),
    ROW("row"),
    SEAT("seat"),
    GATE("gate"),
    SECTOR("sector"),
    PRICE_ZONE("price_zone");

    private final String code;

    ExternalBarcodesField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static ExternalBarcodesField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
