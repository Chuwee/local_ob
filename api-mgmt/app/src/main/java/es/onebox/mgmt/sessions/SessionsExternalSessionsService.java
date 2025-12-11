package es.onebox.mgmt.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalSessionConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionExternalSessions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.converters.SessionConverter;
import es.onebox.mgmt.sessions.dto.UpdateSessionExternalSessionsRequestDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionsExternalSessionsService {

    private final ValidationService validationService;
    private final EventsRepository eventsRepository;

    @Autowired
    public SessionsExternalSessionsService(EventsRepository eventsRepository, ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
    }

    public ExternalSessionConfig getSessionExternalSessions(Long eventId, Long sessionId) {
        validationService.getAndCheckSession(eventId, sessionId);
        return eventsRepository.getSessionExternalSessions(sessionId);
    }

    public void updateSessionExternalSessions(Long eventId, Long sessionId, UpdateSessionExternalSessionsRequestDTO updateRequest) {
        Event event = validationService.getAndCheckEvent(eventId);
        if (!event.getType().equals(EventType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_IS_NOT_AVET);
        }
        validationService.getAndCheckSession(eventId, sessionId);
        SessionExternalSessions sessionExternalDTO = SessionConverter.toMsEvent(updateRequest);
        sessionExternalDTO.setId(sessionId);
        eventsRepository.updateSessionExternalSessions(sessionId, sessionExternalDTO);
    }

}
