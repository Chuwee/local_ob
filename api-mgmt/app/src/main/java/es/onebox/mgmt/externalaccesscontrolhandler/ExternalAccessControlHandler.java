package es.onebox.mgmt.externalaccesscontrolhandler;

import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.sessions.dto.CloneSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionRequestDTO;

import java.util.List;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;

import java.time.ZonedDateTime;

public interface ExternalAccessControlHandler {

    void validateCreateSeasonTicket(Integer customCategoryId, ZonedDateTime startDate, ZonedDateTime endDate);
    void createSeasonTicket(Long entityId ,Long seasonTicketId);
    void assignSessionToSeasonTicket(SeasonTicket seasonTicket, Long sessionId);
    void unassignSessionFromSeasonTicket(SeasonTicket seasonTicket, Long sessionId);
    void addOrUpdateEventRate(Long entityId, Long eventId, Long rateId);
    void createSessions(Event event, List<Long> sessionId);
    void validateCreateSession(Event event, CreateSessionRequestDTO request);
    void validateCloneSession(Event event, CloneSessionRequestDTO request);
    void addOrUpdateVenueElements(Long entityId, Long venueTemplateId);
}
