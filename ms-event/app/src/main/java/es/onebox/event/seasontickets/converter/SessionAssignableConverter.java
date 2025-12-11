package es.onebox.event.seasontickets.converter;

import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.seasontickets.dto.SessionAssignableDTO;
import es.onebox.event.seasontickets.dto.SessionAssignableReason;
import es.onebox.event.seasontickets.dto.SessionAssignationStatusDTO;
import es.onebox.event.seasontickets.dto.SessionResultDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import org.apache.commons.lang3.BooleanUtils;

public class SessionAssignableConverter {

    private SessionAssignableConverter() {

    }

    public static SessionAssignableDTO convert(SessionResultDTO sessionResult) {
        SessionAssignableDTO result = new SessionAssignableDTO();
        if (SessionPackType.RESTRICTED.equals(sessionResult.getEventSeasonType())) {
            result.setAssignable(Boolean.FALSE);
            result.setReason(SessionAssignableReason.SESSION_ASSIGNABLE_RESTRICTED);
        } else {
            Boolean assignable = isAssignable(sessionResult);
            result.setAssignable(assignable);
            if (BooleanUtils.isFalse(assignable)) {
                result.setReason(SessionAssignableReason.SESSION_ASSIGNABLE_INVALID_STATUS);
            }
        }
        return result;
    }

    public static Boolean isAssignable(SessionResultDTO sessionResult) {
        EventStatus eventStatus = EventStatus.byId(sessionResult.getEventStatus());
        SessionStatus sessionStatus = SessionStatus.byId(sessionResult.getSessionStatus());
        return isAssignable(eventStatus, sessionStatus);
    }

    public static Boolean isAssignable(EventStatus eventStatus, SessionStatus sessionStatus) {
        boolean result = false;
        switch (sessionStatus) {
            case PLANNED:
            case SCHEDULED:
                result = sessionPlanned(eventStatus);
                break;
            case READY:
            case PREVIEW:
                result = sessionReady(eventStatus);
                break;
            default:
                break;
        }
        return result;
    }

    private static Boolean sessionPlanned(EventStatus eventStatus) {
        boolean result = false;
        switch (eventStatus) {
            case PLANNED:
            case IN_PROGRAMMING:
            case READY:
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    private static Boolean sessionReady(EventStatus eventStatus) {
        boolean result = false;
        switch (eventStatus) {
            case PLANNED:
            case IN_PROGRAMMING:
                result = true;
                break;
            case READY:
            default:
                break;
        }
        return result;
    }

    public static SessionAssignationStatusDTO setStatus(SessionResultDTO sessionResult, Integer seasonTicketSessionId) {
        return sessionResult.getRelatedSeasonSessionIds() != null &&
                sessionResult.getRelatedSeasonSessionIds().contains(seasonTicketSessionId) ?
                SessionAssignationStatusDTO.ASSIGNED : SessionAssignationStatusDTO.NOT_ASSIGNED;
    }
}

