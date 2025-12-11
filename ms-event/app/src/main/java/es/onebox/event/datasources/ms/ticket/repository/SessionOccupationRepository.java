package es.onebox.event.datasources.ms.ticket.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.onebox.event.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationsSearchRequest;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionVenueContainerSearchRequest;

@Repository
public class SessionOccupationRepository {

    private final MsTicketDatasource msTicketDatasource;

    @Autowired
    public SessionOccupationRepository(MsTicketDatasource msTicketDatasource) {
        this.msTicketDatasource = msTicketDatasource;
    }

    public List<SessionOccupationByPriceZoneDTO> searchOccupationsByPriceZones(SessionOccupationsSearchRequest request) {
        return msTicketDatasource.searchSessionOccupationsByPriceZones(request);
    }

    public List<SessionOccupationVenueContainer> searchOccupationsByContainer(Long sessionId, SessionVenueContainerSearchRequest request) {
        return msTicketDatasource.searchOccupationsByContainer(sessionId, request);
    }

    public Long countSessionOccupationsByPriceZones(Long sessionId, Long priceZoneId) {
        return msTicketDatasource.countSessionOccupationsByPriceZones(sessionId, priceZoneId);
    }
}
