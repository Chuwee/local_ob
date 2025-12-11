package es.onebox.event.sessions.enums;

import java.util.stream.Stream;

public enum PresaleStatus {
    DELETED(0),
    ACTIVE(1),
    INACTIVE(2);

    private final Integer id;

    PresaleStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PresaleStatus byId(Integer id) {
        return Stream.of(PresaleStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
