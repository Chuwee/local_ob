package es.onebox.common.datasources.ms.event.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.event.MsEventDatasource;
import es.onebox.common.datasources.ms.event.dto.AttendantsConfigDTO;
import es.onebox.common.datasources.ms.event.dto.AttendantsFields;
import es.onebox.common.datasources.ms.event.dto.ChannelEventDTO;
import es.onebox.common.datasources.ms.event.dto.CommunicationElement;
import es.onebox.common.datasources.ms.event.dto.EventChannelDTO;
import es.onebox.common.datasources.ms.event.dto.EventChannelSurchargesDTO;
import es.onebox.common.datasources.ms.event.dto.EventChannelsDTO;
import es.onebox.common.datasources.ms.event.dto.EventCommunicationElementDTO;
import es.onebox.common.datasources.ms.event.dto.EventCommunicationElementFilter;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.EventRatesDTO;
import es.onebox.common.datasources.ms.event.dto.EventTemplatePriceDTO;
import es.onebox.common.datasources.ms.event.dto.EventsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductChannelDTO;
import es.onebox.common.datasources.ms.event.dto.ProductChannelsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductDTO;
import es.onebox.common.datasources.ms.event.dto.ProductEvents;
import es.onebox.common.datasources.ms.event.dto.ProductLanguages;
import es.onebox.common.datasources.ms.event.dto.ProductPublishingSessions;
import es.onebox.common.datasources.ms.event.dto.ProductSurchargeDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariants;
import es.onebox.common.datasources.ms.event.dto.RateDTO;
import es.onebox.common.datasources.ms.event.dto.SessionConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.common.datasources.ms.event.dto.SurchargesDTO;
import es.onebox.common.datasources.ms.event.dto.TicketCommunicationElementDTO;
import es.onebox.common.datasources.ms.event.dto.TicketTemplateLiteral;
import es.onebox.common.datasources.ms.event.dto.UpdatePostBookingQuestions;
import es.onebox.common.datasources.ms.event.dto.response.catalog.event.EventCatalog;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.dto.response.session.passbook.SessionPassbookCommElement;
import es.onebox.common.datasources.ms.event.dto.response.session.secmkt.SessionSecMktConfig;
import es.onebox.common.datasources.ms.event.enums.DigitalTicketMode;
import es.onebox.common.datasources.ms.event.request.EventSearchFilter;
import es.onebox.common.datasources.ms.event.request.SessionSearchFilter;
import es.onebox.common.datasources.ms.event.request.UpdateSessionRequest;
import es.onebox.common.datasources.ms.event.request.UpdateSessionsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class MsEventRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public MsEventRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    @Cached(key = "eventTicketCommElement", expires = 10 * 60)
    public List<TicketCommunicationElementDTO> getEventTicketCommunicationElements(@CachedArg Long eventId,
                                                                                   @CachedArg Long languageId,
                                                                                   @CachedArg String type) {
        return msEventDatasource.getEventTicketCommunicationElements(eventId, languageId, type);
    }

    @Cached(key = "sessionTicketCommElement", expires = 10 * 60)
    public List<TicketCommunicationElementDTO> getSessionTicketCommunicationElements(@CachedArg Long eventId,
                                                                                     @CachedArg Long sessionId,
                                                                                     @CachedArg Long languageId,
                                                                                     @CachedArg String type) {
        return msEventDatasource.getSessionTicketCommunicationElements(eventId, sessionId, languageId, type);
    }

    public EventsDTO search(EventSearchFilter eventsFilter) {
        return msEventDatasource.search(eventsFilter);
    }

    public Map<Integer, Map<Integer, List<Integer>>> getAttributes(List<Long> eventIds) {
        return msEventDatasource.getAttributes(eventIds);
    }

    @Cached(key = "MsEventRepository_getSession", expires = 10 * 60)
    public SessionDTO getSession(@CachedArg Long eventId, @CachedArg Long sessionId) {
        return msEventDatasource.getSession(eventId, sessionId);
    }

    @Cached(key = "MsEventRepository_getSessions", expires = 10 * 60)
    public SessionsDTO getSessions(@CachedArg Long eventId, @CachedArg List<Long> sessionIds) {
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setId(sessionIds);
        sessionSearchFilter.setLimit((long) sessionIds.size());

        return msEventDatasource.getSessions(eventId, sessionSearchFilter);
    }

    public SessionsDTO getSessions(SessionSearchFilter filter) {
        return msEventDatasource.getSessions(filter);
    }

    public Map<Long, String> updateSessions(Long eventId, UpdateSessionsRequest updateSessionsRequest) {
        return msEventDatasource.updateSessions(eventId, updateSessionsRequest);
    }

    public void updateSession(Long eventId, Long sessionId, UpdateSessionRequest updateSessionRequest) {
        msEventDatasource.updateSession(eventId, sessionId, updateSessionRequest);
    }

    @Cached(key = "MsEventDatasource_getSession")
    public SessionDTO getSession(@CachedArg Long sessionId) {
        return msEventDatasource.getSession(sessionId);
    }

    @Cached(key = "MsEventRepository_sessionById", expires = 10 * 60)
    public SessionDTO getSessionCached(@CachedArg Long sessionId) {
        return getSession(sessionId);
    }

    @Cached(key = "MsEventDatasource_getSessionConfig")
    public SessionConfigDTO getSessionConfig(@CachedArg Integer sessionId) {
        return msEventDatasource.getSessionConfig(sessionId);
    }

    @Cached(key = "seasonTicketTicketCommElement", expires = 10 * 60)
    public List<TicketCommunicationElementDTO> getSeasonTicketCommunicationElements(@CachedArg Long seasonTicketId,
                                                                                    @CachedArg Long languageId,
                                                                                    @CachedArg String type) {
        return msEventDatasource.getSeasonTicketCommunicationElements(seasonTicketId, languageId, type);
    }

    @Cached(key = "MsEventDatasource_getTicketTemplateLiterals", expires = 10 * 60)
    public List<TicketTemplateLiteral> getTicketTemplateLiterals(@CachedArg Long ticketTemplateId, @CachedArg Long languageId) {
        return msEventDatasource.getTicketTemplateLiterals(ticketTemplateId, languageId);
    }

    @Cached(key = "MsEventDatasource_getTicketTemplateCommElements", expires = 10 * 60)
    public List<CommunicationElement> getTicketTemplateCommElements(@CachedArg Long ticketTemplateId, @CachedArg Long languageId) {
        return msEventDatasource.getTicketTemplateCommElements(ticketTemplateId, languageId);
    }

    public EventChannelsDTO getEventChannels(Long eventId) {
        return msEventDatasource.getEventChannels(eventId);
    }

    public List<SurchargesDTO> getEventSurcharges(Long eventId) {
        return msEventDatasource.getEventSurcharges(eventId);
    }


    public List<EventChannelSurchargesDTO> getEventChannelSurcharges(Long eventId, Long channelId) {
        return msEventDatasource.getEventChannelSurcharges(eventId, channelId);
    }

    public List<EventTemplatePriceDTO> getEventVenueTemplatePrices(Long eventId, Long templateId) {
        return msEventDatasource.getEventVenueTemplatePrices(eventId, templateId);
    }

    public EventRatesDTO getEventRatesDetails(Long eventId) {
        return msEventDatasource.getEventRatesDetails(eventId);
    }

    @Cached(key = "MsEventRepository_getEvent", expires = 10 * 60)
    public EventDTO getEvent(@CachedArg Long eventId) {
        return msEventDatasource.getEventById(eventId);
    }

    @Cached(key = "MsEventRepository_getSeasonTicket", expires = 10 * 60)
    public EventDTO getSeasonTicket(@CachedArg Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketById(seasonTicketId);
    }

    @Cached(key = "MsEventRepository_getEventWithSeasonTicket", expires = 3 * 60)
    public EventsDTO getCachedEventWithSeasonTickets(@CachedArg Long eventId) {
        return getEventWithSeasonTickets(eventId);
    }

    public EventsDTO getEventWithSeasonTickets(Long eventId) {
        EventSearchFilter eventSearchFilter = new EventSearchFilter();
        eventSearchFilter.setId(Arrays.asList(eventId));
        eventSearchFilter.setIncludeSeasonTickets(Boolean.TRUE);

        return msEventDatasource.searchEvents(eventSearchFilter);
    }

    public void updateEvent(Long eventId, EventDTO updateEvent) {
        msEventDatasource.updateEvent(eventId, updateEvent);
    }

    @Cached(key = "MsEventRepository_getRate", expires = 10 * 60)
    public RateDTO getRate(@CachedArg Long eventId, @CachedArg Long rateId) {
        return msEventDatasource.getRate(eventId, rateId);
    }

    public List<EventCommunicationElementDTO> getEventCommunicationElements(Long eventId, EventCommunicationElementFilter filter) {
        return msEventDatasource.getEventCommunicationElements(eventId, filter);
    }

    public List<EventCommunicationElementDTO> getEventChannelCommunicationElements(Long eventId, Long channelId, EventCommunicationElementFilter filter) {
        return msEventDatasource.getEventChannelCommunicationElements(eventId, channelId, filter);
    }

    public List<EventCommunicationElementDTO> getSessionCommunicationElements(Long eventId, Long sessionId, EventCommunicationElementFilter filter) {
        return msEventDatasource.getSessionCommunicationElements(eventId, sessionId, filter);
    }

    public List<EventCommunicationElementDTO> getSessionChannelCommunicationElements(Long eventId, Long sessionId, Long channelId, EventCommunicationElementFilter filter) {
        return msEventDatasource.getSessionChannelCommunicationElements(eventId, sessionId, channelId, filter);
    }

    @Cached(key = "MsEventRepository_getEventChannel", expires = 5 * 60)
    public EventChannelDTO getEventChannel(@CachedArg Long eventId, @CachedArg Long channelId) {
        return msEventDatasource.getEventChannel(eventId, channelId);
    }

    public AttendantsConfigDTO getAttendantsConfig(Long eventId) {
        return msEventDatasource.getAttendantsConfig(eventId);
    }

    public AttendantsFields getAttendantsFields(Long eventId) {
        return msEventDatasource.getAttendantsFields(eventId);
    }

    public ProductDTO getProduct(Long productId) {
        return msEventDatasource.getProduct(productId);
    }

    public ProductLanguages productLanguages(Long productId) {
        return msEventDatasource.getProductLanguages(productId);
    }

    public List<ProductSurchargeDTO> getProductSurcharges(Long productId) {
        return msEventDatasource.getProductSurcharges(productId);
    }

    public ProductVariants getProductVariants(Long productId) {
        return msEventDatasource.getProductVariants(productId);
    }

    public ProductCommunicationElementsTextsDTO getProductCommunicationElementsTexts(Long productId) {
        return msEventDatasource.getProductCommunicationElementsText(productId);
    }

    public ProductCommunicationElementsImagesDTO getProductCommunicationElementImages(Long productId) {
        return msEventDatasource.getProductCommunicationElementsImages(productId);
    }

    public ProductEvents getProductEvents(Long productId) {
        return msEventDatasource.getProductEvents(productId);
    }

    public ProductPublishingSessions geProductSessions(Long productId, Long eventId) {
        return msEventDatasource.getProductSessions(productId, eventId);
    }

    public ProductChannelsDTO getProductChannels(Long productId) {
        return msEventDatasource.getProductChannels(productId);
    }

    public ProductChannelDTO getProductChannel(Long productId, Long channelId) {
        return msEventDatasource.getProductChannel(productId, channelId);
    }

    public void updatePostBookingQuestions(UpdatePostBookingQuestions postBookingQuestions) {

        msEventDatasource.updatePostBookingQuestions(postBookingQuestions);
    }

    public ChannelEventDTO getChannelEvent(Long eventId, Long channelId) {
        return msEventDatasource.getChannelEvent(eventId, channelId);
    }

    @Cached(key = "event_catalog", expires = 3 * 60)
    public EventCatalog getEventCatalog(@CachedArg Long eventId) {
        return msEventDatasource.getEventCatalog(eventId);
    }

    @Cached(key = "session_catalog", expires = 3 * 60)
    public SessionCatalog getSessionCatalog(@CachedArg Long sessionId) {
        return msEventDatasource.getSessionCatalog(sessionId);
    }

    @Cached(key = "session_secmkt_config", expires = 3 * 60)
    public SessionSecMktConfig getSessionSecMktConfig(@CachedArg Long sessionId) {
        return msEventDatasource.getSessionSecMktConfig(sessionId);
    }

    @Cached(key = "session_passbook_elements")
    public List<SessionPassbookCommElement> getSessionPassbookCommElements(@CachedArg Long eventId, @CachedArg Long sessionId) {
        return msEventDatasource.getSessionPassbookCommElements(eventId, sessionId);
    }

    @Cached(key = "session_external_ticket_mode", expires = 3, timeUnit = TimeUnit.MINUTES)
    public DigitalTicketMode getDigitalTicketMode(@CachedArg Long entityId, @CachedArg Long eventId, @CachedArg Long sessionId) {
        return msEventDatasource.getDigitalTicketMode(entityId, eventId, sessionId);
    }

}
