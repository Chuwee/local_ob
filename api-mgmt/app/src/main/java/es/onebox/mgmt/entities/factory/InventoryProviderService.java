package es.onebox.mgmt.entities.factory;

import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoriesList;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CreateSeasonTicketData;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;

public interface InventoryProviderService {

    InventoriesList getExternalInventories(Long entityId, Boolean skipUsed);
    Long createVenueTemplate(CreateVenueTemplateRequest venueTemplateRequest);
    void deleteVenueTemplate(Long entityId, Long venueTemplateId, UpdateVenueTemplate venueTemplate);
    Long createEvent(CreateEventData eventData);
    void deleteEvent(Long entityId, Event event);
    Long createSeasonTicket(CreateSeasonTicketData sessionTicketData);
    void deleteSeasonTicket(Long entityId, SeasonTicket seasonTicket);
    Long createSession(Long eventId, CreateSessionData sessionData);
    void deleteSession(Long eventId, Session updateSession);
    PreSaleConfigDTO createSessionPresale(Long eventId, Long sessionId, PreSaleConfigDTO request, boolean isSeasonTicket);
    void deleteSessionPresale(Long entityId, Long eventId, Long sessionId, Long presaleId, boolean isSeasonTicket);
}
