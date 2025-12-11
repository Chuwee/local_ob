package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.sessions.dto.UpdateSessionTicketContentsBulk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class SessionTicketContentsRepository {

    @Autowired
    private MsEventDatasource msEventDatasource;

    public void updateCommunicationElements(final Long eventId, final Long sessionId, final Set<TicketCommunicationElement> elements,
                                            TicketCommunicationElementCategory category) {
        this.msEventDatasource.updateSessionTicketCommunicationElements(eventId, sessionId, elements, category);
    }

    public void updateCommunicationElementsBulk(final Long eventId, final UpdateSessionTicketContentsBulk dto,
                                            TicketCommunicationElementCategory category) {
        this.msEventDatasource.updateSessionTicketCommunicationElementsBulk(eventId, dto, category);
    }

    public List<TicketCommunicationElement> findCommunicationElements(final Long eventId, final Long sessionId, final CommunicationElementFilter<?> filter, TicketCommunicationElementCategory category) {
        return this.msEventDatasource.getSessionTicketCommunicationElements(eventId, sessionId, filter, category);
    }

    public void deleteCommunicationElements(final Long eventId, final Long sessionId, final String languageCode,
                                            final String tag,  TicketCommunicationElementCategory type){
        this.msEventDatasource.deleteSessionTicketCommunicationElements(eventId, sessionId,languageCode, tag, type);
    }

    public void deleteCommunicationElementBulk(final Long eventId, final List<Long> sessionIds, final String languageCode,
                                            final String tag,  TicketCommunicationElementCategory type){
        this.msEventDatasource.deleteSessionTicketCommunicationElementBulk(eventId, sessionIds,languageCode, tag, type);
    }

    public void deleteImageCommunicationElementsBulk(final Long eventId, final List<Long> sessionIds, final String languageCode,
                                                     TicketCommunicationElementCategory type){
        this.msEventDatasource.deleteImageSessionTicketCommunicationElementsBulk(eventId, sessionIds,languageCode, type);
    }

}
