package es.onebox.event.sessions.dto;

import java.io.Serializable;
import java.util.stream.Stream;

public enum SessionGenerationStatus implements Serializable {
    PENDING(0),
    IN_PROGRESS(1),
    ACTIVE(2),
    ERROR(3);

    private final Integer id;

    SessionGenerationStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static SessionGenerationStatus byId(Integer id) {
        return Stream.of(SessionGenerationStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
