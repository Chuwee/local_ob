package es.onebox.event.events.dto.statusflag;

import java.io.Serializable;

public enum SessionReleaseFlagStatus implements Serializable {
	
	PLANNED, 			// PLANIFICADA
	IN_PROGRAMMING, 	// PROGRAMACION
	RELEASE_PENDING, 	// PENDIENTE_PUBLICAR
	RELEASED, 			// PUBLICADO
	RELEASE_CANCELLED, 	// PUBLICACION_CANCELADA
	RELEASE_FINISHED, 	// PUBLICACION_FINALIZADA
	CANCELLED, 			// CANCELADA
	NOT_ACCOMPLISHED 	// NO_REALIZADA

}
