package es.onebox.mgmt.venues.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum UpdateSeatStatus implements Serializable {

    FREE(1),
    PROMOTOR_LOCKED(3),
    KILL(6);

    private int status;

    public int getStatus() {
        return status;
    }

    UpdateSeatStatus(int status) {
        this.status = status;
    }

    public static UpdateSeatStatus byId(Integer id) {
        return Stream.of(UpdateSeatStatus.values())
                .filter(v -> v.getStatus() == id)
                .findFirst()
                .orElse(null);
    }
}
