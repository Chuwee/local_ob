package es.onebox.mgmt.sessions.dto;

import java.io.Serializable;

public enum SessionSaleFlagStatus implements Serializable {

    PLANNED,
    IN_PROGRAMMING,
    SALE_PENDING,
    SALE_CANCELLED,
    PENDING_SALE_CANCELLED,
    SALE,
    SALE_FINISHED,
    CANCELLED,
    NOT_ACCOMPLISHED

}
