package es.onebox.mgmt.events.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum EventChannelField implements FiltrableField {


    CHANNEL_NAME("name", "channelname");

    private static final long serialVersionUID = 1L;

    String name;
    String dtoName;

    EventChannelField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static EventChannelField byName(String name) {
        return Stream.of(EventChannelField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
