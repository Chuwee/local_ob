package es.onebox.event.events.dto.statusflag;

import java.io.Serializable;

public enum EventChannelSaleFlagStatus implements Serializable  {

    PENDING_RELATIONSHIP,   // PENDIENTE_RELACION
    PLANNED,                // PLANIFICACION
    IN_PROGRAMMING,         // PROGRAMACION
    SALE_PENDING,           // PENDIENTE_VENTA
    SALE,                   // EN_VENTA
    SALE_CANCELLED,         // VENTA_CANCELADA
    RELEASE_FINISHED,       // PUBLICACION_FINALIZADA
    RELEASE_CANCELLED,      // PUBLICACION_CANCELADA
    CANCELLED,              // CANCELADO
    NOT_ACCOMPLISHED,       // NO_REALIZADO
    REJECTED,               // RECHAZADO
    SALE_ONLY_SECONDARY_MARKET; // EN_VENTA_SOLO_EN_MERCADO_SECUNDARIO


}
