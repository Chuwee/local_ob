package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum UpdateSeatStatus implements Serializable {

    FREE(1),
    PROMOTOR_LOCKED(3),
    KILL(6);
    private final int status;

    private static final Map<Integer, UpdateSeatStatus> lookup = new HashMap<>();

    static {
        for (UpdateSeatStatus status : EnumSet.allOf(UpdateSeatStatus.class)) {
            lookup.put(status.getStatus(), status);
        }
    }

    UpdateSeatStatus(int estado) {
        this.status = estado;
    }

    public int getStatus() {
        return status;
    }

    public static UpdateSeatStatus getById(Integer id) {
        return lookup.get(id);
    }

    public static UpdateSeatStatus getById(Long id) {
        return id == null ? null : lookup.get(id.intValue());
    }


}
