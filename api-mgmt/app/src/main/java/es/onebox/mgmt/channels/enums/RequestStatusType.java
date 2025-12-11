package es.onebox.mgmt.channels.enums;

import java.util.stream.Stream;

public enum RequestStatusType {
    REJECTED(0),            // RECHAZADA
    PENDING(1),             // SOLICITADA
    ACCEPTED(2),            // ACEPTADA
    PENDING_REQUEST(3);     // PENDIENTE_SOLICITAR

    private final int id;

    RequestStatusType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static RequestStatusType getById(int id) {
        return Stream.of(values())
                .filter(cs -> cs.id == id)
                .findAny()
                .orElse(null);
    }

}
