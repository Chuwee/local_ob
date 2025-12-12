package es.onebox.common.datasources.ms.event.enums;

import java.io.Serializable;

public enum EventChannelReleaseFlagStatus implements Serializable {

    PENDING_RELATIONSHIP,   // PENDIENTE_RELACION
    PLANNED,                // PLANIFICACION
    IN_PROGRAMMING,         // PROGRAMACION
    RELEASE_PENDING,        // PENDIENTE_PUBLICAR
    RELEASED,               // PUBLICADO
    RELEASE_CANCELLED,      // PUBLICACION_CANCELADA
    RELEASE_FINISHED,       // PUBLICACION_FINALIZADA
    CANCELLED,              // CANCELADO
    NOT_ACCOMPLISHED,       // NO_REALIZADO
    REJECTED;               // RECHAZADA

}
