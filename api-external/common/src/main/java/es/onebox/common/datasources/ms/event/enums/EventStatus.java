package es.onebox.common.datasources.ms.event.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum EventStatus implements Serializable {
    DELETED(0),
    PLANNED(1),
    IN_PROGRAMMING(2),
    READY(3),
    NOT_ACCOMPLISHED(4),
    CANCELLED(5),
    IN_PROGRESS(6),
    FINISHED(7);

    private final Integer id;

    EventStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static EventStatus byId(Integer id) {
        return Stream.of(EventStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
