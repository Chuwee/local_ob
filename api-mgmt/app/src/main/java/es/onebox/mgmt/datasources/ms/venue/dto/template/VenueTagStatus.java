package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum VenueTagStatus implements Serializable {

    FREE(1),
    PROMOTOR_LOCKED(3),
    KILL(6),
    SEASON_LOCKED(14);

    private final int status;

    private static final Map<Integer, VenueTagStatus> lookup = new HashMap<>();

    static {
        for (VenueTagStatus status : EnumSet.allOf(VenueTagStatus.class)) {
            lookup.put(status.getStatus(), status);
        }
    }

    VenueTagStatus(int estado) {
        this.status = estado;
    }

    public int getStatus() {
        return status;
    }

    public static VenueTagStatus getById(Integer id) {
        return lookup.get(id);
    }

    public static VenueTagStatus getById(Long id) {
        return id == null ? null : lookup.get(id.intValue());
    }


}
