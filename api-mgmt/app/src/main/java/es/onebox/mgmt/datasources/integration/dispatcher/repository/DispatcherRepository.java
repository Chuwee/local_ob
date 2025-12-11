package es.onebox.mgmt.datasources.integration.dispatcher.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.integration.dispatcher.IntDispatcherDatasource;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.AforosList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalEventBaseList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalPresaleBaseList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalSessionBaseList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoriesList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.MotivoEmisionSummary;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.PaymentModes;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.RolInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.StatusDTO;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.TermInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.enums.ConnectionType;
import es.onebox.mgmt.datasources.integration.dispatcher.enums.Status;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CreateSeasonTicketData;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;
import es.onebox.mgmt.events.dto.ExternalEventsProviderType;
import es.onebox.mgmt.events.dto.ExternalSessionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DispatcherRepository {

    private static final String CACHE_AVET_KEY_TERMS = "avet.terms";
    private static final String CACHE_AVET_KEY_CAPACITIES = "avet.capacities";
    private static final String CACHE_AVET_KEY_ROLES = "avet.roles";

    private static final String CACHE_AVET_SGA_INVENTORIES = "sga.inventories";
    private static final String CACHE_EXTERNAL_EVENTS = "external.events";
    private static final String CACHE_EXTERNAL_SESSIONS = "external.sessions";
    private static final int CACHE_AVET_TTL = 5;
    private static final int CACHE_SGA_TTL = 1;

    private final IntDispatcherDatasource datasource;

    @Autowired
    public DispatcherRepository(IntDispatcherDatasource datasource) {
        this.datasource = datasource;
    }

    @Cached(key = CACHE_AVET_KEY_TERMS, expires = CACHE_AVET_TTL)
    public TermInfoList getTermsInfo(@CachedArg Long entityId) {
        return datasource.getTermsInfo(entityId);
    }

    @Cached(key = CACHE_AVET_KEY_ROLES, expires = CACHE_AVET_TTL)
    public RolInfoList getRolesInfo(@CachedArg Long entityId, @CachedArg Long capacityId) {
        return datasource.getRolesInfo(entityId, capacityId);
    }

    @Cached(key = CACHE_AVET_KEY_CAPACITIES, expires = CACHE_AVET_TTL)
    public AforosList getAforosInfo(@CachedArg Long entityId) {
        return datasource.getAforosInfo(entityId);
    }

    @Cached(key = CACHE_AVET_SGA_INVENTORIES, expires = CACHE_SGA_TTL)
    public InventoriesList getSgaInventories(@CachedArg Long entityId) {
        return datasource.getExternalInventories(entityId);
    }

    @Cached(key = CACHE_EXTERNAL_EVENTS, expires = CACHE_SGA_TTL)
    public ExternalEventBaseList getExternalEvents(@CachedArg Long entityId, @CachedArg Long venueTemplateId, @CachedArg ExternalEventsProviderType type) {
        return datasource.getExternalEvents(entityId, venueTemplateId, type);
    }

    @Cached(key = CACHE_EXTERNAL_SESSIONS, expires = CACHE_SGA_TTL)
    public ExternalSessionBaseList getExternalSessions(
            @CachedArg Long entityId,
            @CachedArg Long eventId,
            @CachedArg ExternalSessionStatus status
    ) {
        return datasource.getExternalSessions(entityId, eventId, status);
    }

    public StatusDTO getConnectionStatus(Long entityId, ConnectionType connectionType) {
        try {
            return datasource.getConnectionStatus(entityId, connectionType);
        } catch (Exception e) {
            return new StatusDTO(Status.ERROR, "Problem connecting via dispatcher");
        }
    }

    public Long createVenueTemplate(CreateVenueTemplateRequest createVenueTemplateRequest) {
        return datasource.createVenueTemplate(createVenueTemplateRequest);
    }

    public void deleteVenueTemplate(Long entityId, Long venueTemplateId, UpdateVenueTemplate venueTemplate) {
        datasource.deleteVenueTemplate(entityId, venueTemplateId, venueTemplate);
    }

    public Long createEvent(CreateEventData eventData) {
        return datasource.createEvent(eventData);
    }

    public void deleteEvent(Long entityId, Event event) {
        datasource.deleteEvent(entityId, event);
    }

    public Long createSeasonTicket(CreateSeasonTicketData createSeasonTicketData) {
        return datasource.createSeasonTicket(createSeasonTicketData);
    }

    public void deleteSeasonTicket(Long entityId, SeasonTicket seasonTicket) {
        datasource.deleteSeasonTicket(entityId, seasonTicket);
    }

    public Long createSession(Long eventId, CreateSessionData session) {
        return datasource.createSession(eventId, session);
    }

    public void deleteSession(Long eventId, Session updateSession) {
        datasource.deleteSession(eventId, updateSession);
    }

    public void updateSessionInventory(Long entityId, Long eventId, Long sessionId, Boolean isSmartBooking) {
        datasource.updateSessionInventory(entityId, eventId, sessionId, isSmartBooking);
    }

    public void updateSeasonTicketInventory(Long entityId, Long seasonTicketId) {
        datasource.updateSeasonTicketInventory(entityId, seasonTicketId);
    }

    public void updateActivityInventory(Long entityId, Long eventId) {
        datasource.updateActivityInventory(entityId, eventId);
    }

    public MotivoEmisionSummary getEmissionReasons(Long entityId) {
        return datasource.getEmissionReasons(entityId);
    }

    public PaymentModes getPaymentModes(Long entityId) {
        return datasource.getPaymentModes(entityId);
    }

    public ExternalPresaleBaseList getExternalPresales(Long entityId, Long eventId, Long sessionId, boolean skipUsed) {
        return datasource.getExternalPresales(entityId, eventId, sessionId, skipUsed);
    }

    public PreSaleConfigDTO createExternalPresale(Long eventId, Long sessionId, PreSaleConfigDTO body, boolean isSeason) {
        return datasource.createPresale(eventId, sessionId, body, isSeason);
    }

    public void deleteExternalPresale(Long entityId, Long eventId, Long sessionId, Long presaleId, boolean isSeason) {
        datasource.deletePresale(entityId, eventId, sessionId, presaleId, isSeason);
    }

    public ExternalPresaleBaseList getExternalSeasonTicketPresales(Long entityId, Long seasonTicketId, boolean skipUsed) {
        return datasource.getExternalSeasonTicketPresales(entityId, seasonTicketId, skipUsed);
    }
}
