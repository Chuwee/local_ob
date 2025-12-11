package es.onebox.event.seasontickets.service;

import es.onebox.event.common.services.CommonCommunicationElementService;
import es.onebox.event.common.services.CommonTicketCommunicationElementService;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.dto.PassbookCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.request.PassbookCommunicationElementFilter;
import es.onebox.jooq.annotation.MySQLWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class SeasonTicketCommunicationElementService {

    private final SeasonTicketService seasonTicketService;
    private final CommonCommunicationElementService commonCommunicationElementService;
    private final CommonTicketCommunicationElementService commonTicketCommunicationElementService;

    @Autowired
    public SeasonTicketCommunicationElementService(SeasonTicketService seasonTicketService,
                                                   CommonCommunicationElementService commonCommunicationElementService,
                                                   CommonTicketCommunicationElementService commonTicketCommunicationElementService) {
        this.seasonTicketService = seasonTicketService;
        this.commonCommunicationElementService = commonCommunicationElementService;
        this.commonTicketCommunicationElementService = commonTicketCommunicationElementService;
    }


    public List<EventCommunicationElementDTO> findCommunicationElements(Long seasonTicketId, EventCommunicationElementFilter filter) {
        EventRecord seasonTicketEvent = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        return commonCommunicationElementService.findCommunicationElements(seasonTicketId, filter, seasonTicketEvent);
    }

    @MySQLWrite
    public void updateCommunicationElements(Long seasonTicketId, List<EventCommunicationElementDTO> elements) {
        EventRecord seasonTicketEvent = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonCommunicationElementService.updateCommunicationElements(seasonTicketId, elements, seasonTicketEvent);
    }

    public List<PassbookCommunicationElementDTO> getPassbookCommunicationElements(Long seasonTicketId, PassbookCommunicationElementFilter filter) {
        return commonTicketCommunicationElementService.getEventPassbookCommunicationElements(filter, seasonTicketId);
    }


    public void updatePassbookCommunicationElements(Long seasonTicketId, HashSet<PassbookCommunicationElementDTO> elements) {
        EventRecord seasonTicketEvent = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonTicketCommunicationElementService.updatePassbookCommElements(elements, seasonTicketEvent);
    }

    @MySQLWrite
    public void deleteCommunicationElement(final Long seasonTicketId, TicketCommunicationElementTagType tag, String language, TicketCommunicationElementCategory type) {
        EventRecord seasonTicketEvent = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonTicketCommunicationElementService.deleteCommunicationElement(tag, language, type, seasonTicketEvent);
    }

    public void deletePassbookCommunicationElement(final Long seasonTicketId, PassbookCommunicationElementTagType tag, String language) {
        commonTicketCommunicationElementService.deleteEventPassbookCommElement(tag, language, seasonTicketId);
    }
}
