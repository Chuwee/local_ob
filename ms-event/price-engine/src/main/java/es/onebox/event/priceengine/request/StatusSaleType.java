package es.onebox.event.priceengine.request;

import java.util.stream.Stream;

public enum StatusSaleType {
    PENDING_RELATIONSHIP(0),   // PENDIENTE_RELACION
    PLANNED(1),                // PLANIFICACION
    IN_PROGRAMMING(2),         // PROGRAMACION
    SALE_PENDING(3),           // PENDIENTE_VENTA
    SALE(4),                   // EN_VENTA
    SALE_CANCELLED(5),         // VENTA_CANCELADA
    RELEASE_FINISHED(6),       // PUBLICACION_FINALIZADA
    RELEASE_CANCELLED(7),      // PUBLICACION_CANCELADA
    CANCELLED(8),              // CANCELADO
    NOT_ACCOMPLISHED(9),       // NO_REALIZADO
    REJECTED(10);              // RECHAZADO

    private int id;

    StatusSaleType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static StatusSaleType getById(int id) {
        return Stream.of(values())
                .filter(cs -> cs.id == id)
                .findAny()
                .orElse(null);
    }
}
