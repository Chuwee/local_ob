package es.onebox.event.events.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum TourStatus implements Serializable {

    DELETED(0),
    ACTIVE(1),
    INACTIVE(2);

    private final Integer id;

    TourStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TourStatus byId(Integer id) {
        return Stream.of(TourStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
