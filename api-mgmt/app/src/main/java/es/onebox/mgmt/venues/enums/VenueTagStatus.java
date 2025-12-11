package es.onebox.mgmt.venues.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum VenueTagStatus implements Serializable {

    FREE(1),
    SOLD(2),
    PROMOTOR_LOCKED(3),
    KILL(6),
    GIFT(13),
    SEASON_LOCKED(14),
    EXTERNAL_LOCKED(15);

    private int status;

    public int getStatus() {
        return status;
    }

    VenueTagStatus(int status) {
        this.status = status;
    }

    public static VenueTagStatus byId(Integer id) {
        return Stream.of(VenueTagStatus.values())
                .filter(v -> v.getStatus() == id)
                .findFirst()
                .orElse(null);
    }

    public static VenueTagStatus byName(String name) {
        return Stream.of(VenueTagStatus.values())
                .filter(v -> v.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
