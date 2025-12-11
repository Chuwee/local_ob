package es.onebox.mgmt.sessions.enums;

import es.onebox.mgmt.datasources.ms.event.dto.TicketStatus;

import java.util.Objects;

public enum SessionRefundConditionsTicketStatus {

    FREE(1),
    PROMOTOR_LOCKED(3),
    KILL(6);

    private int status;

    public int getStatus() {
        return status;
    }

    SessionRefundConditionsTicketStatus(int status) {
        this.status = status;
    }

    public static SessionRefundConditionsTicketStatus byTicketStatus(TicketStatus status) {
        if(Objects.isNull(status)){
            return null;
        }

        switch(status){
            case AVAILABLE:
                return FREE;
            case KILL:
                return KILL;
            case BLOCKED_PROMOTER:
                return PROMOTOR_LOCKED;
            default:
                return null;
        }
    }
}
