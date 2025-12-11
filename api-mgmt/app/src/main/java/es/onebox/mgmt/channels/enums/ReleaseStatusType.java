package es.onebox.mgmt.channels.enums;

import java.util.stream.Stream;

public enum ReleaseStatusType {
    PENDING_RELATIONSHIP(0),   // PENDIENTE_RELACION
    PLANNED(1),                // PLANIFICACION
    IN_PROGRAMMING(2),         // PROGRAMACION
    RELEASE_PENDING(3),        // PENDIENTE_PUBLICAR
    RELEASED(4),               // PUBLICADO
    RELEASE_CANCELLED(5),      // PUBLICACION_CANCELADA
    RELEASE_FINISHED(6),       // PUBLICACION_FINALIZADA
    CANCELLED(7),              // CANCELADO
    NOT_ACCOMPLISHED(8),       // NO_REALIZADO
    REJECTED(9);               // RECHAZADA

    private final int id;

    ReleaseStatusType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ReleaseStatusType getById(int id) {
        return Stream.of(values())
                .filter(cs -> cs.id == id)
                .findAny()
                .orElse(null);
    }

}
