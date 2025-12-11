package es.onebox.mgmt.venues.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum SeatStatus implements Serializable {

    FREE(1),
    SOLD(2),
    PROMOTOR_LOCKED(3),
    SYSTEM_LOCKED(4),
    BOOKED(5),
    KILL(6),
    EMITTED(7),
    VALIDATED(8),
    IN_REFUND(9),
    CANCELLED(10),
    PRESOLD_LOCKED(11),
    SOLD_LOCKED(12),
    GIFT(13),
    SEASON_LOCKED(14),
    EXTERNAL_LOCKED(15),
    EXTERNAL_DELETE(16);

    private int status;

    public int getStatus() {
        return status;
    }

    SeatStatus(int status) {
        this.status = status;
    }

    public static SeatStatus byId(Integer id) {
        return Stream.of(SeatStatus.values())
                .filter(v -> v.getStatus() == id)
                .findFirst()
                .orElse(null);
    }

}
