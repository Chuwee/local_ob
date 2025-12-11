package es.onebox.event.sessions.dto;

import java.io.Serializable;
import java.util.stream.Stream;

public enum SeatDeleteStatus implements Serializable {
    FREE(1),
    LOCKED(3);

    private final Integer id;

    SeatDeleteStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static SeatDeleteStatus byId(Integer id) {
        return Stream.of(SeatDeleteStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

