package es.onebox.event.events.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum EventChannelStatus implements Serializable {

    REJECTED(0),            // RECHAZADA
    PENDING(1),             // SOLICITADA
    ACCEPTED(2),            // ACEPTADA
    PENDING_REQUEST(3);     // PENDIENTE_SOLICITAR

    private final Integer id;

    EventChannelStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static EventChannelStatus byId(Integer id) {
        return Stream.of(EventChannelStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
