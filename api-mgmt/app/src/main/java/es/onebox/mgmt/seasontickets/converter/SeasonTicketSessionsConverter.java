package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSession;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSessionsSummary;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionDTO;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventResponse;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketAssignationStatus;
import es.onebox.mgmt.timezone.converter.TimeZoneConverter;

public class SeasonTicketSessionsConverter {

    private SeasonTicketSessionsConverter() {
    }

    public static SeasonTicketSessionDTO fromMsEvent(SeasonTicketSession seasonTicketSession) {
        if (seasonTicketSession == null) {
            return null;
        }

        SeasonTicketSessionDTO seasonTicketSessionDTO = new SeasonTicketSessionDTO();
        seasonTicketSessionDTO.setSessionId(seasonTicketSession.getSessionId());
        seasonTicketSessionDTO.setSessionName(seasonTicketSession.getSessionName());
        seasonTicketSessionDTO.setEventId(seasonTicketSession.getEventId());
        seasonTicketSessionDTO.setEventName(seasonTicketSession.getEventName());
        seasonTicketSessionDTO.setSessionAssignable(seasonTicketSession.getSessionAssignable());
        seasonTicketSessionDTO.setSessionStartingDate(seasonTicketSession.getSessionStartingDate());
        seasonTicketSessionDTO.setSessionStartingDateTZ(TimeZoneConverter.fromEntity(seasonTicketSession.getSessionStartingDateTZ()));
        seasonTicketSessionDTO.setStatus(SeasonTicketAssignationStatus.valueOf(seasonTicketSession.getStatus().name()));

        return seasonTicketSessionDTO;
    }

    public static SeasonTicketSessionsSummary fromMsEvent(es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessionsSummary summary) {
        if(summary == null) {
            return null;
        }
        SeasonTicketSessionsSummary summaryDTO = new SeasonTicketSessionsSummary();
        summaryDTO.setListedEvents(summary.getListedEvents());
        summaryDTO.setTotalSessions(summary.getTotalSessions());
        summaryDTO.setSessionsOnSale(summary.getSessionsOnSale());
        summaryDTO.setAssignedSessions(summary.getAssignedSessions());
        return summaryDTO;
    }

    public static SeasonTicketSessionsEventResponse fromMsEvent(IdNameDTO dto) {
        SeasonTicketSessionsEventResponse event = new SeasonTicketSessionsEventResponse();
        event.setEventId(dto.getId());
        event.setEventName(dto.getName());
        return event;
    }
}
