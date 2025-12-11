package es.onebox.event.seasontickets.service;

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
public class SeasonTicketTicketCommunicationElementService {

    private final SeasonTicketService seasonTicketService;
    private final CommonTicketCommunicationElementService commonTicketCommunicationElementService;

    @Autowired
    public SeasonTicketTicketCommunicationElementService(SeasonTicketService seasonTicketService,
                                                         CommonTicketCommunicationElementService commonTicketCommunicationElementService) {
        this.seasonTicketService = seasonTicketService;
        this.commonTicketCommunicationElementService = commonTicketCommunicationElementService;
    }

    @MySQLRead
    public List<TicketCommunicationElementDTO> getSeasonTicketCommunicationElements(Long seasonTicketId, TicketCommunicationElementFilter filter, TicketCommunicationElementCategory type) {
        EventRecord seasonTicketRecord = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        return commonTicketCommunicationElementService.getCommunicationElements(filter, type, seasonTicketRecord);
    }

    public List<PassbookCommunicationElementDTO> getSeasonTicketPassbookCommunicationElements(Long seasonTicketId, PassbookCommunicationElementFilter filter) {
        return commonTicketCommunicationElementService.getEventPassbookCommunicationElements(filter, seasonTicketId);
    }

    @MySQLWrite
    public void updateSeasonTicketCommunicationElements(Long seasonTicketId, Set<TicketCommunicationElementDTO> elements, TicketCommunicationElementCategory type) {
        final EventRecord seasonTicketRecord = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonTicketCommunicationElementService.updateTicketCommElements(elements, type, seasonTicketRecord);
    }

    public void updateEventPassbookCommunicationElements(Long seasonTicketId, Set<PassbookCommunicationElementDTO> elements) {
        final EventRecord event = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonTicketCommunicationElementService.updatePassbookCommElements(elements, event);
    }

    @MySQLWrite
    public void deleteEventCommunicationElement(final Long seasonTicketId, TicketCommunicationElementTagType tag, String language, TicketCommunicationElementCategory type) {
        final EventRecord seasonTicketRecord = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonTicketCommunicationElementService.deleteCommunicationElement(tag, language, type, seasonTicketRecord);
    }

    public void deleteEventPassbookCommunicationElement(final Long eventId, PassbookCommunicationElementTagType tag, String language) {
        commonTicketCommunicationElementService.deleteEventPassbookCommElement(tag, language, eventId);
    }
}
