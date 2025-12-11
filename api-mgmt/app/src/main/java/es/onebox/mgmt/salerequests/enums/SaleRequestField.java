package es.onebox.mgmt.salerequests.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum SaleRequestField implements FiltrableField {

    STATUS("status", "status"),
    CHANNEL_ID("channel.id", "channel.id"),
    CHANNEL_NAME("channel.name", "channel.name"),
    EVENT_ID("event.id", "event.id"),
    EVENT_NAME("event.name", "event.name"),
    EVENT_STARTDATE("event.start_date", "event.startDate");

    private static final long serialVersionUID = 1L;

    String name;
    String dtoName;

    SaleRequestField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static SaleRequestField byName(String name) {
        return Stream.of(SaleRequestField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
