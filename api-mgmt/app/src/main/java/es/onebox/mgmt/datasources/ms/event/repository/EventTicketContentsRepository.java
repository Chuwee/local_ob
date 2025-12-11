package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class EventTicketContentsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public EventTicketContentsRepository(final MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public List<TicketCommunicationElement> findCommunicationElements(final Long eventId, final CommunicationElementFilter<?> filter, TicketCommunicationElementCategory category) {
        return this.msEventDatasource.getEventTicketCommunicationElements(eventId, filter, category);
    }

    public void updateCommunicationElements(final Long eventId, final Set<TicketCommunicationElement> elements, TicketCommunicationElementCategory category) {
        this.msEventDatasource.updateEventTicketCommunicationElements(eventId, elements, category);
    }

    public void deleteCommunicationElementImage(final Long eventId, String language, String type, TicketCommunicationElementCategory category) {
        this.msEventDatasource.deleteEventTicketCommunicationElement(eventId, language, type, category);
    }
}
