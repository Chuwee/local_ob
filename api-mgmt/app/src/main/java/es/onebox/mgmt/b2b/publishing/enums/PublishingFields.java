package es.onebox.mgmt.b2b.publishing.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum PublishingFields implements FiltrableField {

    SESSION("session.name", "additionalData.sessionName"),
    EVENT("event.name", "additionalData.eventName"),
    CLIENT("client.name", "additionalData.clientName"),
    DATE("date", "date"),
    PRICE("price", "additionalData.price"),
    CHANNEL("channel.name", "channel.name");

    private static final long serialVersionUID = 1L;

    String name;
    String dtoName;

    PublishingFields(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static es.onebox.mgmt.b2b.publishing.enums.PublishingFields byName(String name) {
        return Stream.of(es.onebox.mgmt.b2b.publishing.enums.PublishingFields.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
