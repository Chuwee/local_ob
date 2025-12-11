package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import java.io.Serializable;
import java.util.stream.Stream;

public enum PackChannelStatus implements Serializable {

    REJECTED(0),            // RECHAZADA
    PENDING(1),             // SOLICITADA
    ACCEPTED(2),            // ACEPTADA
    PENDING_REQUEST(3);     // PENDIENTE_SOLICITAR

    private final Integer id;

    PackChannelStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static PackChannelStatus byId(Integer id) {
        return Stream.of(PackChannelStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
