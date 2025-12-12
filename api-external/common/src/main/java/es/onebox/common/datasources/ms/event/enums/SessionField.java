package es.onebox.common.datasources.ms.event.enums;

import es.onebox.common.datasources.common.dto.FiltrableField;

import java.util.stream.Stream;

public enum SessionField implements FiltrableField {

    ID("id", "id"),
    NAME("name", "name"),
    STATUS("status", "status"),
    TYPE("type", "type"),
    STARTDATE("start_date", "date.start");

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String dtoName;

    SessionField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public String getName() {
        return name;
    }

    public static SessionField byName(String name) {
        return Stream.of(SessionField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }
}
