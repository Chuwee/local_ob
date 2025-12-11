package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;

public enum EventStatus implements Serializable {
    DELETED,
    PLANNED,
    IN_PROGRAMMING,
    READY,
    NOT_ACCOMPLISHED,
    CANCELLED,
    FINISHED
}
