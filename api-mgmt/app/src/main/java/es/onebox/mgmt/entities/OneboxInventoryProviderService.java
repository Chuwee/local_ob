package es.onebox.mgmt.entities;

import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoriesList;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CreateSeasonTicketData;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OneboxInventoryProviderService implements InventoryProviderService {

    private final VenuesRepository venuesRepository;
    private final EventsRepository eventsRepository;
    private final SeasonTicketRepository seasonTicketRepository;

    @Autowired
    public OneboxInventoryProviderService(VenuesRepository venuesRepository, EventsRepository eventsRepository, SeasonTicketRepository seasonTicketRepository) {
        this.venuesRepository = venuesRepository;
        this.eventsRepository = eventsRepository;
        this.seasonTicketRepository = seasonTicketRepository;
    }

    @Override
    public InventoriesList getExternalInventories(Long entityId, Boolean skipUsed) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public Long createVenueTemplate(CreateVenueTemplateRequest venueTemplateRequest) {
        return venuesRepository.createVenueTemplate(venueTemplateRequest);
    }

    @Override
    public void deleteVenueTemplate(Long entityId, Long venueTemplateId, UpdateVenueTemplate venueTemplate) {
        venuesRepository.updateVenueTemplate(venueTemplateId, venueTemplate);
    }

    @Override
    public Long createEvent(CreateEventData eventData) {
        return eventsRepository.create(eventData);
    }

    @Override
    public void deleteEvent(Long entityId, Event event) {
        eventsRepository.updateEvent(event);
    }

    @Override
    public Long createSeasonTicket(CreateSeasonTicketData createSeasonTicketData) {
        return seasonTicketRepository.create(createSeasonTicketData);
    }

    @Override
    public void deleteSeasonTicket(Long entityId, SeasonTicket seasonTicket) {
        seasonTicketRepository.deleteSeasonTicket(seasonTicket.getId());
    }

    @Override
    public Long createSession(Long eventId, CreateSessionData sessionData) {
        return eventsRepository.createSession(eventId, sessionData);
    }

    @Override
    public void deleteSession(Long eventId, Session updateSession) {
        eventsRepository.updateSession(eventId, updateSession);
    }

    @Override
    public PreSaleConfigDTO createSessionPresale(Long eventId, Long sessionId, PreSaleConfigDTO request, boolean isSeasonTicket) {
        return eventsRepository.createSessionPreSale(eventId, sessionId, request);
    }

    @Override
    public void deleteSessionPresale(Long entityId, Long eventId, Long sessionId, Long presaleId, boolean isSeasonTicket) {
        eventsRepository.deleteSessionPreSale(eventId, sessionId, presaleId);
    }
}
