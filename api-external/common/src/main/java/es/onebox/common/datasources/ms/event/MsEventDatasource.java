package es.onebox.common.datasources.ms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.common.converters.ConvertUtils;
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
import es.onebox.common.datasources.ms.event.dto.PackDTO;
import es.onebox.common.datasources.ms.event.dto.ProductChannelDTO;
import es.onebox.common.datasources.ms.event.dto.ProductChannelsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductDTO;
import es.onebox.common.datasources.ms.event.dto.ProductEvents;
import es.onebox.common.datasources.ms.event.dto.ProductPublishingSessions;
import es.onebox.common.datasources.ms.event.dto.ProductLanguages;
import es.onebox.common.datasources.ms.event.dto.ProductSurchargeDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import es.onebox.common.datasources.ms.event.dto.ProductVariants;
import es.onebox.common.datasources.ms.event.dto.RateDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketPrice;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsFilter;
import es.onebox.common.datasources.ms.event.dto.SessionConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.common.datasources.ms.event.dto.SurchargesDTO;
import es.onebox.common.datasources.ms.event.dto.TicketCommunicationElementDTO;
import es.onebox.common.datasources.ms.event.dto.TicketTemplateLiteral;
import es.onebox.common.datasources.ms.event.dto.UpdatePostBookingQuestions;
import es.onebox.common.datasources.ms.event.dto.UpdateRenewalRequest;
import es.onebox.common.datasources.ms.event.dto.UpdateSeasonTicketAutomaticRenewalStatus;
import es.onebox.common.datasources.ms.event.dto.response.catalog.event.EventCatalog;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.dto.response.session.passbook.SessionPassbookCommElement;
import es.onebox.common.datasources.ms.event.dto.response.session.secmkt.SessionSecMktConfig;
import es.onebox.common.datasources.ms.event.enums.DigitalTicketMode;
import es.onebox.common.datasources.ms.event.enums.EventStatus;
import es.onebox.common.datasources.ms.event.enums.SessionField;
import es.onebox.common.datasources.ms.event.enums.SessionStatus;
import es.onebox.common.datasources.ms.event.enums.SessionType;
import es.onebox.common.datasources.ms.event.request.EventSearchFilter;
import es.onebox.common.datasources.ms.event.request.SessionSearchFilter;
import es.onebox.common.datasources.ms.event.request.UpdateSessionRequest;
import es.onebox.common.datasources.ms.event.request.UpdateSessionsRequest;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MsEventDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/events-api/" + API_VERSION;
    private static final String EVENT_TICKET_COMM_ELE_URL = "/events/{eventId}/ticket-communication-elements/{type}";
    private static final String SESSION_TICKET_COMM_ELE_URL = "/events/{eventId}/sessions/{sessionId}/ticket-communication-elements/{type}";
    private static final String SESSION_COMM_ELE_URL = "/events/{eventId}/sessions/{sessionId}/communication-elements";
    private static final String SESSION_CHANNEL_COMM_ELE_URL = "/events/{eventId}/sessions/{sessionId}/channels/{channelId}/communication-elements";
    private static final String EVENTS = "/events";
    protected static final String EVENT_ID = "/{eventId}";
    private static final String RATE = EVENTS + EVENT_ID + "/rates/{rateId}";
    protected static final String SESSIONS = "/sessions";
    protected static final String SESSION_ID = "/{sessionId}";
    private static final String SESSION_CONFIG = SESSIONS + SESSION_ID + "/config";
    private static final String EVENTS_ATTRIBUTES = "/events/attributes";
    protected static final String BULK = "/bulk";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String SEASON_TICKET_TICKET_COMM_ELE_URL = "/season-tickets/{seasonTicketId}/ticket-communication-elements/{type}";
    private static final String TICKET_BASE_URL = "/ticket-templates/{ticketTemplateId}";
    private static final String TICKET_LITERALS_URL = TICKET_BASE_URL + "/literals";
    private static final String COMMUNICATION_ELEMENTS = "/communication-elements";
    private static final String TICKET_COMM_ELEMENTS_URL = TICKET_BASE_URL + COMMUNICATION_ELEMENTS;
    private static final String EVENT_COMMUNICATION_ELEMENTS = EVENTS + EVENT_ID + COMMUNICATION_ELEMENTS;
    private static final String EVENT_CHANNEL = "/events/{eventId}/channels/{channelId}";
    private static final String PRODUCTS_VARIANTS = "/products/{productId}/variants";
    private static final String VARIANT_ID = "/{variantId}";
    private static final String ATTENDANTS_CONFIG = EVENTS + EVENT_ID + "/attendants";
    private static final String ATTENDANTS_FIELDS = EVENTS + EVENT_ID + "/fields";
    private static final String SEASON_TICKET = "/season-tickets/{seasonTicketId}";
    private static final String SEASON_TICKET_RENEWALS = SEASON_TICKET + "/renewals";
    private static final String SEASON_TICKET_RENEWAL_CONFIG = SEASON_TICKET + "/renewals/config";
    private static final String SEASON_TICKET_AUTOMATIC_RENEWAL = SEASON_TICKET + "/renewals/automatic";
    private static final String POST_BOOKING_QUESTIONS = "/post-booking-questions";
    private static final String PRODUCTS = "/products";
    private static final String PRODUCT_ID = PRODUCTS + "/{productId}";
    private static final String SURCHARGES = PRODUCT_ID + "/surcharges";
    protected static final String PRODUCTS_LANGUAGES = PRODUCT_ID + "/languages";
    private static final String PRODUCT_EVENTS = PRODUCT_ID + "/events";
    private static final String PRODUCT_EVENT_SESSIONS = PRODUCT_EVENTS + "/{eventId}/publishing-sessions";
    private static final String PRODUCT_COMM_BASE = PRODUCT_ID + "/communication-elements";
    private static final String PRODUCT_LITERALS = PRODUCT_COMM_BASE + "/texts";
    private static final String PRODUCT_IMAGES = PRODUCT_COMM_BASE + "/images";
    protected static final String CHANNELS = "/channels";
    protected static final String CHANNEL_ID = CHANNELS + "/{channelId}";
    private static final String CATALOG = "/catalog";
    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("500MO000", ApiExternalErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("404ME001", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("400G0001", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("QUESTION_ID_ALREADY_EXISTS", ApiExternalErrorCode.QUESTION_ID_ALREADY_EXISTS);
        ERROR_CODES.put("ME0006", ApiExternalErrorCode.LANGUAGE_NOT_AVAILABLE);
        ERROR_CODES.put("PRODUCT_NOT_FOUND", ApiExternalErrorCode.PRODUCT_NOT_FOUND);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsEventDatasource(@Value("${clients.services.ms-event}") String baseUrl,
                             ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(30000)
                .build();
    }

    public List<TicketCommunicationElementDTO> getEventTicketCommunicationElements(Long eventId, Long languageId,
                                                                                   String type) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_TICKET_COMM_ELE_URL)
                .pathParams(eventId, type)
                .params(new QueryParameters.Builder().addQueryParameter("languageId", languageId).build())
                .execute(ListType.of(TicketCommunicationElementDTO.class));
    }

    public List<TicketCommunicationElementDTO> getSessionTicketCommunicationElements(Long eventId, Long sessionId,
                                                                                     Long languageId, String type) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_TICKET_COMM_ELE_URL)
                .pathParams(eventId, sessionId, type)
                .params(new QueryParameters.Builder().addQueryParameter("languageId", languageId).build())
                .execute(ListType.of(TicketCommunicationElementDTO.class));
    }


    public EventsDTO search(EventSearchFilter eventsFilter) {
        QueryParameters.Builder params = new QueryParameters.Builder();

        fillOperatorAndEntity(params, eventsFilter.getOperatorId(), eventsFilter.getEntityId());

        params.addQueryParameter(LIMIT, eventsFilter.getLimit());
        params.addQueryParameter(OFFSET, eventsFilter.getOffset());
        if (eventsFilter.getId() != null) {
            for (Long eventId : eventsFilter.getId()) {
                params.addQueryParameter("id", eventId);
            }
        }
        params.addQueryParameter("name", eventsFilter.getName());
        params.addQueryParameter("externalReference", eventsFilter.getExternalReference());
        if (CollectionUtils.isNotEmpty(eventsFilter.getVenueId())) {
            for (Long venueId : eventsFilter.getVenueId()) {
                params.addQueryParameter("venueId", venueId);
            }
        }

        addDateParameters(eventsFilter.getStartDate(), eventsFilter.getEndDate(), params);
        addSessionDateParameters(eventsFilter.getSessionStartDate(), eventsFilter.getSessionEndDate(), params);
        addStatusParameters(eventsFilter.getStatus(), eventsFilter.getSessionStatus(), params);

        return httpClient.buildRequest(HttpMethod.GET, EVENTS)
                .params(params.build())
                .execute(EventsDTO.class);
    }

    public Map<Integer, Map<Integer, List<Integer>>> getAttributes(List<Long> eventIds) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        for (Long eventId : eventIds) {
            params.addQueryParameter("eventId", eventId);
        }

        return httpClient.buildRequest(HttpMethod.GET, EVENTS_ATTRIBUTES)
                .params(params.build())
                .execute(HashMap.class);
    }

    public EventChannelsDTO getEventChannels(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}/channels")
                .pathParams(eventId)
                .execute(EventChannelsDTO.class);
    }

    public List<EventChannelSurchargesDTO> getEventChannelSurcharges(Long eventId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}/channels/{channelId}/surcharges")
                .pathParams(eventId, channelId)
                .execute(ListType.of(EventChannelSurchargesDTO.class));
    }

    public List<EventCommunicationElementDTO> getEventChannelCommunicationElements(Long eventId, Long channelId, EventCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}/channels/{channelId}/communication-elements")
                .pathParams(eventId, channelId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElementDTO.class));
    }

    public List<SurchargesDTO> getEventSurcharges(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}/surcharges")
                .pathParams(eventId)
                .execute(ListType.of(SurchargesDTO.class));
    }

    public List<EventTemplatePriceDTO> getEventVenueTemplatePrices(Long eventId, Long templateId) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}/venue-templates/{templateId}/prices")
                .pathParams(eventId, templateId)
                .execute(ListType.of(EventTemplatePriceDTO.class));
    }

    public EventRatesDTO getEventRatesDetails(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}/rates")
                .pathParams(eventId)
                .execute(EventRatesDTO.class);
    }

    public void updateEvent(Long eventId, EventDTO updateEvent) {
        httpClient.buildRequest(HttpMethod.PUT, "/events/{eventId}")
                .pathParams(eventId)
                .body(new ClientRequestBody(updateEvent))
                .execute();
    }

    private void addStatusParameters(List<EventStatus> status, List<SessionStatus> sessionStatus, QueryParameters.Builder params) {
        if (status != null) {
            for (EventStatus eventStatus : status) {
                params.addQueryParameter("status", eventStatus.name());
            }
        }
        if (sessionStatus != null) {
            for (SessionStatus sessionStatusStatus : sessionStatus) {
                params.addQueryParameter("sessionStatus", sessionStatusStatus.name());
            }
        }
    }

    private void fillOperatorAndEntity(QueryParameters.Builder params, Long operatorId, Long entityId) {
        if (operatorId == null) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, "Operator is mandatory field", null);
        }
        params.addQueryParameter("operatorId", operatorId)
                .addQueryParameter("entityId", entityId);
    }

    private void addDateParameters(List<FilterWithOperator<ZonedDateTime>> startDate, List<FilterWithOperator<ZonedDateTime>> endDate, QueryParameters.Builder params) {
        if (startDate != null) {
            for (FilterWithOperator<ZonedDateTime> startDateFilter : startDate) {
                params.addQueryParameter("startDate", serializeFilterDateOperator(startDateFilter));
            }
        }
        if (endDate != null) {
            for (FilterWithOperator<ZonedDateTime> endDateFilter : endDate) {
                params.addQueryParameter("endDate", serializeFilterDateOperator(endDateFilter));
            }
        }
    }

    private void addSessionDateParameters(ZonedDateTime sessionStartDate, ZonedDateTime sessionEndDate, QueryParameters.Builder params) {
        if (sessionStartDate != null) {
            params.addQueryParameter("sessionStartDate", DateUtils.formatISODateTime(sessionStartDate));
        }

        if (sessionEndDate != null) {
            params.addQueryParameter("sessionEndDate", DateUtils.formatISODateTime(sessionEndDate));
        }
    }

    private String serializeFilterDateOperator(FilterWithOperator<ZonedDateTime> filter) {
        String strOperator = "";
        if (filter.getOperator() != null) {
            strOperator = filter.getOperator().getKey() + ":";
        }
        return strOperator + DateUtils.formatISODateTime(filter.getValue());
    }

    public EventDTO getEventById(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}")
                .pathParams(eventId)
                .execute(EventDTO.class);
    }

    public EventDTO getSeasonTicketById(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, "/season-tickets/{seasonTicketId}")
                .pathParams(seasonTicketId)
                .execute(EventDTO.class);
    }

    public EventsDTO searchEvents(EventSearchFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(EventsDTO.class);
    }

    public SessionDTO getSession(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID)
                .pathParams(eventId, sessionId)
                .execute(SessionDTO.class);
    }

    public SessionsDTO getSessions(Long eventId, SessionSearchFilter filter) {
        QueryParameters.Builder params = fillGetSessionsFilter(filter);

        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS)
                .pathParams(eventId)
                .params(params.build())
                .execute(SessionsDTO.class);
    }

    public SessionsDTO getSessions(SessionSearchFilter filter) {
        QueryParameters.Builder params = fillGetSessionsFilter(filter);

        return httpClient.buildRequest(HttpMethod.GET, SESSIONS)
                .params(params.build())
                .execute(SessionsDTO.class);
    }

    public Map<Long, String> updateSessions(Long eventId, UpdateSessionsRequest updateSessionsRequest) {
        return httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + BULK)
                .pathParams(eventId)
                .body(new ClientRequestBody(updateSessionsRequest))
                .execute(HashMap.class);
    }

    public void updateSession(Long eventId, Long sessionId, UpdateSessionRequest updateSessionRequest) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + SESSION_ID)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(updateSessionRequest))
                .execute(HashMap.class);
    }

    public SessionDTO getSession(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSIONS + SESSION_ID)
                .pathParams(sessionId)
                .execute(SessionDTO.class);
    }

    public SessionConfigDTO getSessionConfig(Integer sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_CONFIG)
                .pathParams(sessionId)
                .execute(SessionConfigDTO.class);
    }

    public List<TicketCommunicationElementDTO> getSeasonTicketCommunicationElements(Long seasonTicketId,
                                                                                    Long languageId, String type) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_TICKET_COMM_ELE_URL)
                .pathParams(seasonTicketId, type)
                .params(new QueryParameters.Builder().addQueryParameter("languageId", languageId).build())
                .execute(ListType.of(TicketCommunicationElementDTO.class));
    }

    public List<TicketTemplateLiteral> getTicketTemplateLiterals(Long ticketTemplateId, Long languageId) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_LITERALS_URL)
                .pathParams(ticketTemplateId)
                .params(new QueryParameters.Builder().addQueryParameter("languageId", languageId).build())
                .execute(ListType.of(TicketTemplateLiteral.class));
    }

    public List<CommunicationElement> getTicketTemplateCommElements(Long ticketTemplateId, Long languageId) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_COMM_ELEMENTS_URL)
                .pathParams(ticketTemplateId)
                .params(new QueryParameters.Builder().addQueryParameter("languageId", languageId).build())
                .execute(ListType.of(CommunicationElement.class));
    }

    public ProductVariant getProductVariant(Long productId, Long variantId) {
        return httpClient
                .buildRequest(HttpMethod.GET, PRODUCTS_VARIANTS + VARIANT_ID)
                .pathParams(productId, variantId)
                .execute(ProductVariant.class);
    }

    public ProductVariants getProductVariants(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_VARIANTS)
                .pathParams(productId)
                .execute(ProductVariants.class);
    }

    public PackDTO getPack(Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, "/packs/{packId}")
                .pathParams(packId)
                .execute(PackDTO.class);
    }

    private QueryParameters.Builder fillGetSessionsFilter(SessionSearchFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();

        if (CollectionUtils.isNotEmpty(filter.getId())) {
            filter.getId().forEach(id -> params.addQueryParameter("ids", id));
        }
        if (CollectionUtils.isNotEmpty(filter.getStartDate())) {
            filter.getStartDate().forEach(op -> params.addQueryParameter("rangeDateFrom", op.getValue()));
        }
        if (CollectionUtils.isNotEmpty(filter.getEndDate())) {
            filter.getEndDate().forEach(op -> params.addQueryParameter("rangeDateTo", op.getValue()));
        }
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            for (SessionStatus eventStatus : filter.getStatus()) {
                params.addQueryParameter("status", eventStatus.name());
            }
        }
        if (CollectionUtils.isNotEmpty(filter.getType())) {
            for (SessionType sessionType : filter.getType()) {
                params.addQueryParameter("type", sessionType.name());
            }
        }
        if (CollectionUtils.isNotEmpty(filter.getEventId())) {
            for (Long eventId : filter.getEventId()) {
                params.addQueryParameter("eventId", eventId);
            }
        }
        if (CollectionUtils.isNotEmpty(filter.getVenueId())) {
            for (Long venueId : filter.getVenueId()) {
                params.addQueryParameter("venueId", venueId);
            }
        }
        if (CollectionUtils.isNotEmpty(filter.getAccessValidationSpaceIds())) {
            for (Long accessValidationSpaceId : filter.getAccessValidationSpaceIds()) {
                params.addQueryParameter("accessValidationSpaceId", accessValidationSpaceId);
            }
        }
        if (filter.getEntityId() != null) {
            params.addQueryParameter("entityId", filter.getEntityId());
        }
        if (filter.getOperatorId() != null) {
            params.addQueryParameter("operatorId", filter.getOperatorId());
        }

        params.addQueryParameter(LIMIT, filter.getLimit());
        params.addQueryParameter(OFFSET, filter.getOffset());

        ConvertUtils.checkSortFields(filter.getSort(), params, SessionField::byName);

        return params;
    }

    public RateDTO getRate(Long eventId, Long rateId) {
        return httpClient.buildRequest(HttpMethod.GET, RATE)
                .pathParams(eventId, rateId)
                .execute(RateDTO.class);
    }

    public List<EventCommunicationElementDTO> getSessionCommunicationElements(Long eventId, Long sessionId, EventCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_COMM_ELE_URL)
                .pathParams(eventId, sessionId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElementDTO.class));
    }

    public List<EventCommunicationElementDTO> getSessionChannelCommunicationElements(Long eventId, Long sessionId, Long channelId, EventCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_CHANNEL_COMM_ELE_URL)
                .pathParams(eventId, sessionId, channelId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElementDTO.class));
    }

    public List<EventCommunicationElementDTO> getEventCommunicationElements(Long eventId, EventCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_COMMUNICATION_ELEMENTS)
                .pathParams(eventId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElementDTO.class));
    }

    public EventChannelDTO getEventChannel(Long eventId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_CHANNEL)
                .pathParams(eventId, channelId)
                .execute(EventChannelDTO.class);
    }

    public SeasonTicketDTO getSeasonTicket(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketDTO.class);
    }

    public SeasonTicketRenewalsDTO getSeasonTicketRenewals(Long seasonTicketId, SeasonTicketRenewalsFilter filter) {
        QueryParameters params = new QueryParameters.Builder().addQueryParameters(filter).build();
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_RENEWALS)
                .pathParams(seasonTicketId)
                .params(params)
                .execute(SeasonTicketRenewalsDTO.class);
    }

    public void updateSeasonTicketRenewals(Long seasonTicketId, UpdateRenewalRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_RENEWALS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public SeasonTicketRenewalConfigDTO getSeasonTicketRenewalConfig(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_RENEWAL_CONFIG)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketRenewalConfigDTO.class);
    }

    public void updateSeasonTicketRenewalStatus(Long seasonTicketId, UpdateSeasonTicketAutomaticRenewalStatus body) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_AUTOMATIC_RENEWAL)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public AttendantsConfigDTO getAttendantsConfig(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, ATTENDANTS_CONFIG)
                .pathParams(eventId)
                .execute(AttendantsConfigDTO.class);
    }

    public AttendantsFields getAttendantsFields(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, ATTENDANTS_FIELDS)
                .pathParams(eventId)
                .execute(AttendantsFields.class);
    }

    public ProductDTO getProduct(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_ID)
                .pathParams(productId)
                .execute(ProductDTO.class);
    }

    public ProductLanguages getProductLanguages(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_LANGUAGES)
                .pathParams(productId)
                .execute(ProductLanguages.class);
    }

    public ProductChannelsDTO getProductChannels(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_ID + CHANNELS)
                .pathParams(productId)
                .execute(ProductChannelsDTO.class);
    }

    public ProductChannelDTO getProductChannel(Long productId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_ID + CHANNEL_ID)
                .pathParams(productId, channelId)
                .execute(ProductChannelDTO.class);
    }

    public List<ProductSurchargeDTO> getProductSurcharges(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, SURCHARGES)
                .pathParams(productId)
                .execute(ListType.of(ProductSurchargeDTO.class));
    }

    public ProductCommunicationElementsTextsDTO getProductCommunicationElementsText(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_LITERALS)
                .pathParams(productId)
                .execute(ProductCommunicationElementsTextsDTO.class);
    }

    public ProductCommunicationElementsImagesDTO getProductCommunicationElementsImages(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_IMAGES)
                .pathParams(productId)
                .execute(ProductCommunicationElementsImagesDTO.class);
    }

    public ProductEvents getProductEvents(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_EVENTS)
                .pathParams(productId)
                .execute(ProductEvents.class);
    }

    public ProductPublishingSessions getProductSessions(Long productId, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_EVENT_SESSIONS)
                .pathParams(productId, eventId)
                .execute(ProductPublishingSessions.class);
    }

    public void updatePostBookingQuestions(UpdatePostBookingQuestions request) {
        httpClient.buildRequest(HttpMethod.PUT, POST_BOOKING_QUESTIONS)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public ChannelEventDTO getChannelEvent(Long eventId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CATALOG + EVENT_CHANNEL)
                .pathParams(eventId, channelId)
                .execute(ChannelEventDTO.class);
    }

    public EventCatalog getEventCatalog(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, CATALOG + "/events/{eventId}")
                .pathParams(eventId)
                .execute(EventCatalog.class);
    }

    public SessionCatalog getSessionCatalog(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, CATALOG + "/sessions/{sessionId}")
                .pathParams(sessionId)
                .execute(SessionCatalog.class);
    }

    public SessionSecMktConfig getSessionSecMktConfig(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, "/secondary-market/session-config/{sessionId}")
                .pathParams(sessionId)
                .execute(SessionSecMktConfig.class);
    }

    public List<SessionPassbookCommElement> getSessionPassbookCommElements(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/{eventId}/sessions/{sessionId}/ticket-communication-elements/PASSBOOK")
                .pathParams(eventId, sessionId)
                .execute(ListType.of(SessionPassbookCommElement.class));
    }

    public DigitalTicketMode getDigitalTicketMode(Long entityId, Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}/events/{eventId}/sessions/{sessionId}/external-ticket-mode")
                .pathParams(entityId, eventId, sessionId)
                .execute(DigitalTicketMode.class);
    }

    public List<SeasonTicketPrice> getSeasonTicketPrices(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET + "/prices")
                .pathParams(seasonTicketId)
                .execute(ListType.of(SeasonTicketPrice.class));
    }
}
