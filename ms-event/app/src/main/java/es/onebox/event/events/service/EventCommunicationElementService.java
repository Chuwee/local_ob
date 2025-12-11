package es.onebox.event.events.service;

import es.onebox.event.common.services.CommonCommunicationElementService;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.jooq.annotation.MySQLWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCommunicationElementService {

    public static final int DEFAULT_COMELEMENT_POSITION = 1;

    private final EventService eventService;
    private final CommonCommunicationElementService commonCommunicationElementService;

    @Autowired
    public EventCommunicationElementService(EventService eventService, CommonCommunicationElementService commonCommunicationElementService) {
        this.eventService = eventService;
        this.commonCommunicationElementService = commonCommunicationElementService;
    }


    public List<EventCommunicationElementDTO> findCommunicationElements(Long eventId, EventCommunicationElementFilter filter) {
        EventRecord event = eventService.getAndCheckEvent(eventId);
        return commonCommunicationElementService.findCommunicationElements(eventId, filter, event);
    }

    @MySQLWrite
    public void updateCommunicationElements(Long eventId, List<EventCommunicationElementDTO> elements) {
        EventRecord event = eventService.getAndCheckEvent(eventId);
        commonCommunicationElementService.updateCommunicationElements(eventId, elements, event);
    }
}
