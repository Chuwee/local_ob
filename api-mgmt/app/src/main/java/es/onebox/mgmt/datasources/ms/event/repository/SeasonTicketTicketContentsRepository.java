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
public class SeasonTicketTicketContentsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public SeasonTicketTicketContentsRepository(final MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public List<TicketCommunicationElement> findCommunicationElements(final Long seasonTicketId, final CommunicationElementFilter<?> filter, TicketCommunicationElementCategory category) {
        return this.msEventDatasource.getSeasonTicketTicketCommunicationElements(seasonTicketId, filter, category);
    }

    public void updateCommunicationElements(final Long seasonTicketId, final Set<TicketCommunicationElement> elements, TicketCommunicationElementCategory category) {
        this.msEventDatasource.updateSeasonTicketTicketCommunicationElements(seasonTicketId, elements, category);
    }

    public void deleteCommunicationElementImage(final Long seasonTicketId, String language, String type, TicketCommunicationElementCategory category) {
        this.msEventDatasource.deleteSeasonTicketTicketCommunicationElement(seasonTicketId, language, type, category);
    }
}
