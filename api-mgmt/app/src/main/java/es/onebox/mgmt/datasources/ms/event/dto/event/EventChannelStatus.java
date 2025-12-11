package es.onebox.mgmt.datasources.ms.event.dto.event;

public enum EventChannelStatus {

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

}
