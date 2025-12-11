package es.onebox.event.events.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum VenueStatusDTO implements Serializable {
    DELETED(0),
    ACTIVE(1),
    PROCESSING(2),
    ERROR(3);

    private final Integer id;

    VenueStatusDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static VenueStatusDTO byId(Integer id) {
        return Stream.of(VenueStatusDTO.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static VenueStatusDTO byId(Byte id) {
        return id == null ? null : byId(id.intValue());
    }
}
