package es.onebox.bepass.events;

import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.bepass.common.BepassCacheService;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.bepass.datasources.bepass.dto.Event;
import es.onebox.bepass.datasources.bepass.dto.EventResponse;
import es.onebox.bepass.datasources.bepass.repository.BepassEventsRepository;
import es.onebox.bepass.events.converter.BepassEventConverter;
import es.onebox.bepass.events.dto.CreateEventDTO;
import es.onebox.bepass.events.dto.EventDTO;
import es.onebox.bepass.events.dto.UpdateEventDTO;
import es.onebox.common.datasources.ms.event.request.UpdateSessionRequest;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BepassEventsService {

    private final BepassEventsRepository bepassEventsRepository;
    private final BepassCacheService bepassCacheService;
    private final MsEventRepository eventRepository;

    public BepassEventsService(BepassEventsRepository bepassEventsRepository,
                               BepassCacheService bepassCacheService,
                               MsEventRepository eventRepository) {
        this.bepassEventsRepository = bepassEventsRepository;
        this.bepassCacheService = bepassCacheService;
        this.eventRepository = eventRepository;
    }

    public List<EventDTO> searchEvents() {
        List<Event> events = bepassEventsRepository.getEvents();
        return events.stream().map(BepassEventConverter::toEventDTO).collect(Collectors.toList());
    }

    public List<Event> searchBepassRawEvents() {
        return bepassEventsRepository.getEvents();
    }

    public EventResponse createEvent(CreateEventDTO body) {
        SessionDTO session = this.eventRepository.getSession(body.sessionId());
        EventResponse response = this.bepassEventsRepository.createEvent(BepassEventConverter.toCreateEvent(session, body.locationId()));
        this.upsertExternalMapping(response.getEvent().getId(), session, session.getId());
        return response;
    }

    public EventResponse updateEvent(String id, UpdateEventDTO body) {
        return this.bepassEventsRepository.updateEvent(id, BepassEventConverter.toUpdateEvent(body));
    }

    public String extractOrCreateExternalEvent(SessionDTO session) {
        Long sessionId = session.getId();
        String sessionReference = session.getReference();

        String externalEventId = bepassCacheService.getExternalEventId(sessionId);
        if (StringUtils.isNotEmpty(externalEventId)) {
            return externalEventId;
        }
        if (StringUtils.isNotEmpty(sessionReference)) {
            bepassCacheService.mapExternalEventId(sessionReference, sessionId);
            return sessionReference;
        }

        List<Event> events = searchBepassRawEvents();
        Event event = events.stream().filter(e -> e.getExternalId().equals(sessionId.toString())).findFirst().orElse(null);
        if (event != null) {
            upsertExternalMapping(event.getId(), session, sessionId);
            return event.getId();
        }

        EventResponse response = createEvent(session);
        return response.getEvent().getId();

    }

    private EventResponse createEvent(SessionDTO session) {
        List<String> locations = BepassAuthContext.get().locationIds();
        if (CollectionUtils.isEmpty(locations) || locations.size() > 1) {
            throw new OneboxRestException(ApiExternalErrorCode.BEPASS_EVENT_NOT_DEFINED);
        }
        EventResponse response = this.bepassEventsRepository.createEvent(BepassEventConverter.toCreateEvent(session, locations.get(0)));
        this.upsertExternalMapping(response.getEvent().getId(), session, session.getId());
        return response;
    }

    private void upsertExternalMapping(String externalEventId, SessionDTO session, Long sessionId) {
        UpdateSessionRequest request = new UpdateSessionRequest();
        request.setReference(externalEventId);
        eventRepository.updateSession(session.getEventId(), session.getId(), request);
        bepassCacheService.mapExternalEventId(externalEventId, sessionId);
    }

}
