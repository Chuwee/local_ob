package es.onebox.mgmt.events.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum EventField implements FiltrableField {

    NAME("name", "name"),
    TYPE("type", "type"),
    STATUS("status", "status"),
    START_DATE("start_date", "date.start"),
    ENTITYID("entity.id", "entity.id");

    private static final long serialVersionUID = 1L;

    String name;
    String dtoName;

    EventField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static EventField byName(String name) {
        return Stream.of(EventField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
