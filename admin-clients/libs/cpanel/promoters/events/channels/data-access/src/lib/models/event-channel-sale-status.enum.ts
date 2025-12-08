export enum EventChannelSaleStatus {
    pendingRelationship = 'PENDING_RELATIONSHIP',  // PENDIENTE_RELACION
    planned = 'PLANNED',                            // PLANIFICACION
    inProgramming = 'IN_PROGRAMMING',              // PROGRAMACION
    salePending = 'SALE_PENDING',                  // PENDIENTE_VENTA
    sale = 'SALE',                                  // EN_VENTA
    saleCancelled = 'SALE_CANCELLED',              // VENTA_CANCELADA
    saleOnlySecondaryMarket = 'SALE_ONLY_SECONDARY_MARKET', // VENTA_SOLO_MERCADO_SECUNDARIO
    releaseFinished = 'RELEASE_FINISHED',          // PUBLICACION_FINALIZADA
    releaseCancelled = 'RELEASE_CANCELLED',        // PUBLICACION_CANCELADA
    cancelled = 'CANCELLED',                        // CANCELADO
    notAccomplished = 'NOT_ACCOMPLISHED',          // NO_REALIZADO
    rejected = 'REJECTED'                          // RECHAZADO
}
