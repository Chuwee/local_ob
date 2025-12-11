package es.onebox.event.events.service;

import es.onebox.event.common.services.CommonEmailCommunicationElementService;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dto.EmailCommunicationElementDTO;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EventEmailCommunicationElementService {

    private final EventService eventService;
    private final CommonEmailCommunicationElementService commonEmailCommunicationElementService;

    @Autowired
    public EventEmailCommunicationElementService(
            EventService eventService, CommonEmailCommunicationElementService commonEmailCommunicationElementService) {
        this.eventService = eventService;
        this.commonEmailCommunicationElementService = commonEmailCommunicationElementService;
    }

    @MySQLRead
    public List<EmailCommunicationElementDTO> findCommunicationElements(final Long eventId, final EmailCommunicationElementFilter filter) {
        final EventRecord event = eventService.getAndCheckEvent(eventId);
        return commonEmailCommunicationElementService.findCommunicationElements(filter, event);
    }

    @MySQLWrite
    public void updateEventCommunicationElements(Long eventId, Set<EmailCommunicationElementDTO> elements) {
        final EventRecord event = eventService.getAndCheckEvent(eventId);
        commonEmailCommunicationElementService.updateEventCommunicationElements(elements, event);
    }

    @MySQLWrite
    public void deleteCommunicationElement(final Long eventId, EmailCommunicationElementTagType tag, String language) {
        final EventRecord event = eventService.getAndCheckEvent(eventId);
        commonEmailCommunicationElementService.deleteCommunicationElement(tag, language, event);
    }
}
