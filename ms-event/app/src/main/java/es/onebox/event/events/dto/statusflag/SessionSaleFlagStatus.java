package es.onebox.event.events.dto.statusflag;

import java.io.Serializable;

public enum SessionSaleFlagStatus implements Serializable {

    PLANNED,                // PLANIFICACION,
    IN_PROGRAMMING,         // PROGRAMACION,
    SALE_PENDING,           // PENDIENTE_VENTA,
    SALE_CANCELLED,         // VENTA_CANCELADA,
    PENDING_SALE_CANCELLED, // VENTA_CANCELADA_PENDIENTE,
    SALE,                   // EN_VENTA,
    SALE_FINISHED,          // VENTA_FINALIZADA,
    CANCELLED,              // CANCELADO,
    NOT_ACCOMPLISHED        // NO_REALIZADO;

}
