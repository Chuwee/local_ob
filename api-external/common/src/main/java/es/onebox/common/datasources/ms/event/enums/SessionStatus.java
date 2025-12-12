package es.onebox.common.datasources.ms.event.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum SessionStatus implements Serializable {
    DELETED(0),
    PLANNED(1),
    SCHEDULED(2),
    READY(3),
    CANCELLED(4),
    NOT_ACCOMPLISHED(5),
    IN_PROGRESS(6),
    FINALIZED(7),
    CANCELLED_EXTERNAL(8),
    PREVIEW(20);

    private final Integer id;

    SessionStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static SessionStatus byId(Integer id) {
        return Stream.of(SessionStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
