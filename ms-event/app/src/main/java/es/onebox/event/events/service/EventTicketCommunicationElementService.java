package es.onebox.event.events.service;

import es.onebox.event.common.services.CommonTicketCommunicationElementService;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dto.PassbookCommunicationElementDTO;
import es.onebox.event.events.dto.TicketCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.PassbookCommunicationElementFilter;
import es.onebox.event.events.request.TicketCommunicationElementFilter;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EventTicketCommunicationElementService {

    private final EventService eventService;
    private final CommonTicketCommunicationElementService commonTicketCommunicationElementService;

    @Autowired
    public EventTicketCommunicationElementService(EventService eventService,
                                                  CommonTicketCommunicationElementService commonTicketCommunicationElementService) {
        this.eventService = eventService;
        this.commonTicketCommunicationElementService = commonTicketCommunicationElementService;
    }

    @MySQLRead
    public List<TicketCommunicationElementDTO> getEventCommunicationElements(Long eventId, TicketCommunicationElementFilter filter, TicketCommunicationElementCategory type) {
        EventRecord event = eventService.getAndCheckEvent(eventId);
        return commonTicketCommunicationElementService.getCommunicationElements(filter, type, event);
    }

    public List<PassbookCommunicationElementDTO> getEventPassbookCommunicationElements(Long eventId, PassbookCommunicationElementFilter filter) {
        return commonTicketCommunicationElementService.getEventPassbookCommunicationElements(filter, eventId);
    }

    @MySQLWrite
    public void updateEventCommunicationElements(Long eventId, Set<TicketCommunicationElementDTO> elements, TicketCommunicationElementCategory type) {
        final EventRecord event = eventService.getAndCheckEvent(eventId);
        commonTicketCommunicationElementService.updateTicketCommElements(elements, type, event);
    }

    public void updateEventPassbookCommunicationElements(Long eventId, Set<PassbookCommunicationElementDTO> elements) {
        final EventRecord event = eventService.getAndCheckEvent(eventId);
        commonTicketCommunicationElementService.updatePassbookCommElements(elements, event);
    }

    @MySQLWrite
    public void deleteEventCommunicationElement(final Long eventId, TicketCommunicationElementTagType tag, String language, TicketCommunicationElementCategory type) {
        final EventRecord event = eventService.getAndCheckEvent(eventId);
        commonTicketCommunicationElementService.deleteCommunicationElement(tag, language, type, event);
    }

    public void deleteEventPassbookCommunicationElement(final Long eventId, PassbookCommunicationElementTagType tag, String language) {
        commonTicketCommunicationElementService.deleteEventPassbookCommElement(tag, language, eventId);
    }
}
