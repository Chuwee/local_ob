package es.onebox.mgmt.entities;

import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoriesList;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CreateSeasonTicketData;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SgaInventoryProviderService implements InventoryProviderService {

    private final DispatcherRepository dispatcherRepository;

    @Autowired
    public SgaInventoryProviderService(DispatcherRepository dispatcherRepository) {
        this.dispatcherRepository = dispatcherRepository;
    }

    @Override
    public InventoriesList getExternalInventories(Long entityId, Boolean skipUsed) {
        if (BooleanUtils.isTrue(skipUsed)) {
            return new InventoriesList(dispatcherRepository.getSgaInventories(entityId)
                    .stream().filter(inventory -> inventory.getInternalId() == null)
                    .toList());
        }
        return dispatcherRepository.getSgaInventories(entityId);
    }

    @Override
    public Long createVenueTemplate(CreateVenueTemplateRequest createVenueTemplateRequest) {
        return dispatcherRepository.createVenueTemplate(createVenueTemplateRequest);
    }

    @Override
    public void deleteVenueTemplate(Long entityId, Long venueTemplateId, UpdateVenueTemplate venueTemplate) {
        dispatcherRepository.deleteVenueTemplate(entityId, venueTemplateId, venueTemplate);
    }

    @Override
    public Long createEvent(CreateEventData eventData) {
        return dispatcherRepository.createEvent(eventData);
    }

    @Override
    public void deleteEvent(Long entityId, Event event) {
        dispatcherRepository.deleteEvent(entityId, event);
    }

    @Override
    public Long createSeasonTicket(CreateSeasonTicketData createSeasonTicketData) {
        return dispatcherRepository.createSeasonTicket(createSeasonTicketData);
    }

    @Override
    public void deleteSeasonTicket(Long entityId, SeasonTicket seasonTicket) {
        dispatcherRepository.deleteSeasonTicket(entityId, seasonTicket);
    }

    @Override
    public Long createSession(Long eventId, CreateSessionData sessionData) {
        return dispatcherRepository.createSession(eventId, sessionData);
    }

    @Override
    public void deleteSession(Long eventId, Session updateSession) {
        dispatcherRepository.deleteSession(eventId, updateSession);
    }

    @Override
    public PreSaleConfigDTO createSessionPresale(Long eventId, Long sessionId, PreSaleConfigDTO request, boolean isSeasonTicket) {
        return dispatcherRepository.createExternalPresale(eventId, sessionId, request, isSeasonTicket);
    }

    @Override
    public void deleteSessionPresale(Long entityId, Long eventId, Long sessionId, Long presaleId, boolean isSeasonTicket) {
        dispatcherRepository.deleteExternalPresale(entityId, eventId, sessionId, presaleId, isSeasonTicket);
    }
}
