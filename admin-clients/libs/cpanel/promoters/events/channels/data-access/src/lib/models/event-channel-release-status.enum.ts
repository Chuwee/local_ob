export enum EventChannelReleaseStatus {
    pendingRelationship = 'PENDING_RELATIONSHIP',  // PENDIENTE_RELACION
    planned = 'PLANNED',                            // PLANIFICACION
    inProgramming = 'IN_PROGRAMMING',              // PROGRAMACION
    releasePending = 'RELEASE_PENDING',            // PENDIENTE_PUBLICAR
    released = 'RELEASED',                          // PUBLICADO
    releaseCancelled = 'RELEASE_CANCELLED',        // PUBLICACION_CANCELADA
    releaseFinished = 'RELEASE_FINISHED',          // PUBLICACION_FINALIZADA
    cancelled = 'CANCELLED',                        // CANCELADO
    notAccomplished = 'NOT_ACCOMPLISHED',          // NO_REALIZADO
    rejected = 'REJECTED'                           // RECHAZADA
}
