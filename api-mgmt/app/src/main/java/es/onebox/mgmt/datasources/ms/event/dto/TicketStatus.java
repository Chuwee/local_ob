package es.onebox.mgmt.datasources.ms.event.dto;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.enums.SessionRefundConditionsTicketStatus;

import java.io.Serializable;
import java.util.Objects;

public enum TicketStatus implements Serializable {

    AVAILABLE,
    SOLD,
    BLOCKED_PROMOTER,
    BLOCKED_SYSTEM,
    BOOKED,
    KILL,
    ISSUED,
    VALIDATED,
    REFUNDED,
    CANCELLED,
    BLOCKED_PRESALE,
    BLOCKED_SALE,
    INVITATION,
    BLOCKED_SEASON_TICKET,
    BLOCKED_EXTERNAL,
    DELETED_EXTERNAL;


    public static TicketStatus getByRefundConditionsTicketStatus(SessionRefundConditionsTicketStatus status){
        if(Objects.isNull(status)){
            throw ExceptionBuilder.build(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Invalid status value");
        }

        switch(status) {
            case FREE:
                return AVAILABLE;
            case PROMOTOR_LOCKED:
                return BLOCKED_PROMOTER;
            case KILL:
                return KILL;
            default:
                throw ExceptionBuilder.build(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Invalid status value");
        }
    }

}
