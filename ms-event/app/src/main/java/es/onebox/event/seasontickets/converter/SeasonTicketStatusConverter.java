package es.onebox.event.seasontickets.converter;

import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import org.apache.commons.lang3.BooleanUtils;

public class SeasonTicketStatusConverter {

    private SeasonTicketStatusConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static EventStatus fromSeasonTicketStatus(SeasonTicketStatusDTO seasonTicketStatus) {
        return switch (seasonTicketStatus) {
            case DELETED -> EventStatus.DELETED;
            case SET_UP -> EventStatus.PLANNED;
            case PENDING_PUBLICATION, READY -> EventStatus.READY;
            case FINISHED -> EventStatus.FINISHED;
            case CANCELLED -> EventStatus.CANCELLED;
        };
    }

    public static SeasonTicketStatusDTO fromSessionStatus(SessionStatus status, Boolean isPreview) {

        return switch (status) {
            case DELETED -> SeasonTicketStatusDTO.DELETED;
            case PLANNED, SCHEDULED -> SeasonTicketStatusDTO.SET_UP;
            case READY ->
                isPreview ? SeasonTicketStatusDTO.PENDING_PUBLICATION : SeasonTicketStatusDTO.READY;
            case CANCELLED, NOT_ACCOMPLISHED, CANCELLED_EXTERNAL -> SeasonTicketStatusDTO.CANCELLED;
            case IN_PROGRESS -> SeasonTicketStatusDTO.READY;
            case FINALIZED -> SeasonTicketStatusDTO.FINISHED;
            case PREVIEW -> SeasonTicketStatusDTO.PENDING_PUBLICATION;
        };
    }

    public static void fromSeasonStatus(SeasonTicketStatusDTO seasonStatus, SessionRecord sessionRecord) {
        SessionStatus sessionStatus;
        int isPreview = 0;

        switch (seasonStatus) {
            case DELETED:
                sessionStatus = SessionStatus.DELETED;
                break;
            case PENDING_PUBLICATION:
                sessionStatus = SessionStatus.READY;
                isPreview = 1;
                break;
            case READY:
                sessionStatus =  SessionStatus.READY;
                break;
            case CANCELLED:
                sessionStatus =  SessionStatus.CANCELLED;
                break;
            case FINISHED:
                sessionStatus =  SessionStatus.FINALIZED;
                break;
            default:
                sessionStatus = SessionStatus.SCHEDULED;
        }

        sessionRecord.setEstado(sessionStatus.getId());
        sessionRecord.setIspreview(BooleanUtils.toBoolean(isPreview));
    }

    public static Boolean checkIsDeleted(SessionRecord sessionRecord) {
        SeasonTicketStatusDTO  status = fromSessionStatus(SessionStatus.byId(sessionRecord.getEstado()),
                sessionRecord.getIspreview());
        return status == SeasonTicketStatusDTO.DELETED;
    }
}
