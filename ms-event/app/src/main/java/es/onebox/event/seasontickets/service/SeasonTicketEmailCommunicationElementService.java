package es.onebox.event.seasontickets.service;

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
public class SeasonTicketEmailCommunicationElementService {

    private final SeasonTicketService seasonTicketService;
    private final CommonEmailCommunicationElementService commonEmailCommunicationElementService;

    @Autowired
    public SeasonTicketEmailCommunicationElementService(SeasonTicketService seasonTicketService,
                                                        CommonEmailCommunicationElementService commonEmailCommunicationElementService) {
        this.seasonTicketService = seasonTicketService;
        this.commonEmailCommunicationElementService = commonEmailCommunicationElementService;
    }

    @MySQLRead
    public List<EmailCommunicationElementDTO> findCommunicationElements(final Long seasonTicketId, final EmailCommunicationElementFilter filter) {
        final EventRecord event = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        return commonEmailCommunicationElementService.findCommunicationElements(filter, event);
    }

    @MySQLWrite
    public void updateSeasonTicketCommunicationElements(Long seasonTicketId, Set<EmailCommunicationElementDTO> elements) {
        final EventRecord seasonTicketEvent = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonEmailCommunicationElementService.updateEventCommunicationElements(elements, seasonTicketEvent);
    }

    @MySQLWrite
    public void deleteCommunicationElement(final Long seasonTicketId, EmailCommunicationElementTagType tag, String language) {
        final EventRecord seasonTicketEvent = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        commonEmailCommunicationElementService.deleteCommunicationElement(tag, language, seasonTicketEvent);
    }
}
