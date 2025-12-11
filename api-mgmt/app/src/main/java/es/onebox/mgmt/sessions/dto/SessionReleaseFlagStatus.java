package es.onebox.mgmt.sessions.dto;

import java.io.Serializable;

public enum SessionReleaseFlagStatus implements Serializable {
	
	PLANNED,
	IN_PROGRAMMING,
	RELEASE_PENDING,
	RELEASED,
	RELEASE_CANCELLED,
	RELEASE_FINISHED,
	CANCELLED,
	NOT_ACCOMPLISHED

}
