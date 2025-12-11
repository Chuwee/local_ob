package es.onebox.mgmt.seasontickets.enums;

import java.io.Serializable;

public enum SeasonTicketStatusDTO implements Serializable {

    SET_UP,
    PENDING_PUBLICATION,
    READY,
    CANCELLED,
    FINISHED;

    private static final long serialVersionUID = 1L;

    SeasonTicketStatusDTO() {
    }
}