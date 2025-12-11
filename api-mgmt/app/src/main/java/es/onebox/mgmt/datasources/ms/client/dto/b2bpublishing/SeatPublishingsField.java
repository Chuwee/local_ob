package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import java.util.Arrays;

public enum SeatPublishingsField {
    ID("id"),
    EVENT_ID("event.id"),
    EVENT_NAME("event.name"),
    SESSION_ID("session.id"),
    SESSION_NAME("session.name"),
    SESSION_DATE("session.date"),
    SEAT_ID("seat.id"),
    VENUE_NAME("seat.venue"),
    SECTOR_NAME("seat.sector"),
    ROW_NAME("seat.row"),
    SEAT_NAME("seat.seat"),
    TYPE("type"),
    PRICE("price"),
    //STATUS("status")//TODO BreakPoint uncomment after a conciliation or webhook is done
    ;

    private final String fieldName;

    SeatPublishingsField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName(){
        return fieldName;
    }

    public static SeatPublishingsField getByName(String name) {
        return Arrays.stream(SeatPublishingsField.values())
                .filter(value -> value.getFieldName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
