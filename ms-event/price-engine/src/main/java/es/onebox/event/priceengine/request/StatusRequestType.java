package es.onebox.event.priceengine.request;

import java.util.stream.Stream;

public enum StatusRequestType {
    REJECTED(0),            // RECHAZADA
    PENDING(1),             // SOLICITADA
    ACCEPTED(2),            // ACEPTADA
    PENDING_REQUEST(3);     // PENDIENTE_SOLICITAR

    private int id;

    StatusRequestType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static StatusRequestType getById(int id) {
        return Stream.of(values())
                .filter(cs -> cs.id == id)
                .findAny()
                .orElse(null);
    }

}
