package es.onebox.mgmt.events.tags.service;

import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagRequest;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.tags.converter.SessionTagConverter;
import es.onebox.mgmt.events.tags.dto.SessionTagRequestDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagResponseDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagsResponseDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

@Service
public class SessionTagService {

    private EventsRepository eventsRepository;
    private ValidationService validationService;

    public SessionTagService(EventsRepository eventsRepository,
                             ValidationService validationService) {
        this.validationService = validationService;
        this.eventsRepository = eventsRepository;
    }

    public SessionTagsResponseDTO getSessionTags(Long eventId, Long sessionId) {
        validationService.getAndCheckSession(eventId, sessionId);
        return SessionTagConverter.toDTO(eventsRepository.getSessionTags(eventId, sessionId));
    }

    public SessionTagResponseDTO createSessionTag(Long eventId, Long sessionId, SessionTagRequestDTO sessionTagRequestDTO) {
        validationService.getAndCheckSession(eventId, sessionId);
        SessionTagRequest request = SessionTagConverter.toMs(sessionTagRequestDTO);
        return SessionTagConverter.toDTO(eventsRepository.createSessionTag(eventId, sessionId, request));
    }

    public void updateSessionTag(Long eventId, Long sessionId, Long positionId, SessionTagRequestDTO sessionTagRequestDTO) {
        validationService.getAndCheckSession(eventId, sessionId);
        eventsRepository.updateSessionTag(eventId, sessionId, positionId, SessionTagConverter.toMs(sessionTagRequestDTO));
    }

    public void deleteSessionTag(Long eventId, Long sessionId, Long positionId) {
        validationService.getAndCheckSession(eventId, sessionId);
        eventsRepository.deleteSessionTag(eventId, sessionId, positionId);
    }
}
