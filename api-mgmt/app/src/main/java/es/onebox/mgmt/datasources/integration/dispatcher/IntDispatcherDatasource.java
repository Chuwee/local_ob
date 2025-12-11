package es.onebox.mgmt.datasources.integration.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
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
import es.onebox.mgmt.datasources.integration.dispatcher.enums.IntegrationType;
import es.onebox.mgmt.datasources.integration.dispatcher.enums.UpdateInventoryType;
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
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.exception.IntegrationHttpExceptionBuilder;
import okhttp3.Interceptor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IntDispatcherDatasource {
    private final HttpClient httpClient;

    //PATH CONSTANTS
    private static final String API_VERSION = "1.0";
    private static final String API_VERSION_V1 = "v1";
    private static final String BASE_PARTNERS_PATH = "/partners-api/" + API_VERSION;
    private static final String BASE_VENUECONFIG_PATH = "/venueconfig-api/" + API_VERSION;
    private static final String BASE_MGMT_PATH = "/mgmt-api/" + API_VERSION_V1;
    private static final String MEMBER_PATH = BASE_PARTNERS_PATH + "/member";
    private static final String GET_ROLES_INFO = MEMBER_PATH + "/roles";
    private static final String GET_TERMS_INFO = MEMBER_PATH + "/terms";
    private static final String GET_AFOROS_INFO = BASE_VENUECONFIG_PATH + "/venues/{venue_id}/capacities";
    private static final String ENTITY_BY_ID = BASE_MGMT_PATH + "/entities/{entityId}";
    private static final String EXTERNAL_INVENTORY = ENTITY_BY_ID + "/inventories";
    private static final String GET_CONNECTION_STATUS = BASE_MGMT_PATH + "/connection/{type}/status";
    private static final String VENUE_TEMPLATE = ENTITY_BY_ID + "/venue-templates";
    private static final String VENUE_TEMPLATE_BY_ID = VENUE_TEMPLATE + "/{venueTemplateId}";
    private static final String EXTERNAL_EVENTS = ENTITY_BY_ID + "/events";
    private static final String EXTERNAL_EVENT_ID = EXTERNAL_EVENTS + "/{eventId}";
    private static final String EXTERNAL_SEASONTICKETS = ENTITY_BY_ID + "/season-tickets";
    private static final String EXTERNAL_SEASONTICKET_ID = EXTERNAL_SEASONTICKETS + "/{seasonTicketId}";
    private static final String EXTERNAL_SEASONTICKET_INVENTORY = EXTERNAL_SEASONTICKET_ID + "/inventory";
    private static final String EXTERNAL_SESSIONS = EXTERNAL_EVENT_ID + "/sessions";
    private static final String EXTERNAL_SESSION_ID = EXTERNAL_SESSIONS + "/{sessionId}";
    private static final String EXTERNAL_SESSION_INVENTORY = EXTERNAL_SESSION_ID + "/inventory";
    private static final String EXTERNAL_EVENT_INVENTORY = EXTERNAL_EVENT_ID + "/inventory";
    private static final String GET_EMISSION_REASONS = ENTITY_BY_ID + "/emission-reasons";
    private static final String GET_PAYMENT_MODES = ENTITY_BY_ID + "/payment-modes";
    private static final String EXTERNAL_PRESALES = EXTERNAL_SESSION_ID + "/presales";
    private static final String EXTERNAL_PRESALE_ID = EXTERNAL_PRESALES + "/{presaleId}";
    //QUERY-CONFIG CONSTANTS-PARAMS
    private static final String QUERY_PARAM_VENUE_ID_UNDERSCORE = "venue_id";
    private static final String QUERY_PARAM_ENTITY_ID = "entityId";
    private static final String QUERY_PARAM_VENUE_TEMPLATE_ID = "venueTemplateId";
    private static final String QUERY_PARAM_EVENT_ID = "eventId";
    private static final String QUERY_PARAM_STATUS = "status";
    private static final String QUERY_PARAM_TYPE = "type";
    private static final String QUERY_PARAM_ST = "is_season";

    //HEADER CONSTANTS
    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_HEADER_VALUE = "application/json";
    private static final String CONTENTTYPE_HEADER = "Content-Type";
    private static final String CONTENTTYPE_VALUE = "application/json";
    private static final int TIMEOUT = 60000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final Map<String, ErrorCode> ERROR_CODES;
    private static final RequestHeaders headers;

    static {
        RequestHeaders.Builder headersBuilder = new RequestHeaders.Builder();
        headersBuilder.addHeader(ACCEPT_HEADER, ACCEPT_HEADER_VALUE);
        headersBuilder.addHeader(CONTENTTYPE_HEADER, CONTENTTYPE_VALUE);
        headers = headersBuilder.build();

        ERROR_CODES = new HashMap<>();
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("NOT_FOUND", ApiMgmtErrorCode.NOT_FOUND);
        ERROR_CODES.put("GENERIC_ERROR", ApiMgmtErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("INVALID_GRANT", ApiMgmtErrorCode.UNAUTHORIZED);
        ERROR_CODES.put("400", ApiMgmtErrorCode.UNAUTHORIZED);
        ERROR_CODES.put("COMMUNICATION_ERROR", ApiMgmtErrorCode.COMMUNICATION_ERROR);
        ERROR_CODES.put("NAME_CONFLICT", ApiMgmtErrorCode.NAME_CONFLICT);
        ERROR_CODES.put("AVET_DATA_INCONSISTENCY", ApiMgmtErrorCode.AVET_DATA_INCONSISTENCY);
        ERROR_CODES.put("AVET_CAPACITY_NOT_ALLOWED", ApiMgmtErrorCode.AVET_CAPACITY_NOT_ALLOWED);

        ERROR_CODES.put("ENTITY_EXTERNAL_CONFIG_INCOMPLETE", ApiMgmtErrorCode.ENTITY_EXTERNAL_CONFIG_INCOMPLETE);
        ERROR_CODES.put("ENTITY_INTEGRATION_CREDENTIALS_NOT_FOUND", ApiMgmtErrorCode.ENTITY_INTEGRATION_CREDENTIALS_NOT_FOUND);

        ERROR_CODES.put("EVENT_MAPPING_NOT_FOUND", ApiMgmtErrorCode.EVENT_MAPPING_NOT_FOUND);
        ERROR_CODES.put("EVENT_ALREADY_EXISTS", ApiMgmtErrorCode.EVENT_ALREADY_EXISTS);
        ERROR_CODES.put("ST_EVENT_ALREADY_EXISTS", ApiMgmtErrorCode.ST_EVENT_ALREADY_EXISTS);
        ERROR_CODES.put("ST_EVENT_NOT_FOUND", ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND);
        ERROR_CODES.put("ST_INVALID_STATUS", ApiMgmtErrorCode.SEASON_TICKET_INVALID_STATUS);
        ERROR_CODES.put("EVENT_CAN_NOT_BE_REMOVED", ApiMgmtErrorCode.FORBIDDEN_EVENT_DELETE);

        ERROR_CODES.put("SESSION_ALREADY_EXISTS", ApiMgmtErrorCode.SESSION_ALREADY_EXISTS);
        ERROR_CODES.put("INVALID_SESSION_DATES_START_REQUIRED", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_START_REQUIRED);
        ERROR_CODES.put("INVALID_SESSION_DATES_END_BEFORE_START", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_END_BEFORE_START);
        ERROR_CODES.put("INVALID_SESSION_DATES_RELEASE_REQUIRED", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_RELEASE_REQUIRED);
        ERROR_CODES.put("INVALID_SESSION_DATES_RELEASE_AFTER_START", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_RELEASE_AFTER_START);
        ERROR_CODES.put("INVALID_SESSION_DATES_RELEASE_AFTER_BOOKING_START", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_RELEASE_AFTER_BOOKING_START);
        ERROR_CODES.put("INVALID_SESSION_DATES_RELEASE_AFTER_SALES_START", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_RELEASE_AFTER_SALES_START);
        ERROR_CODES.put("INVALID_SESSION_DATES_SALES_START_REQUIRED", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_SALES_START_REQUIRED);
        ERROR_CODES.put("INVALID_SESSION_DATES_SALES_START_BEFORE_RELEASE", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_SALES_START_BEFORE_RELEASE);
        ERROR_CODES.put("INVALID_SESSION_DATES_SALES_START_AFTER_START", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_SALES_START_AFTER_START);
        ERROR_CODES.put("INVALID_SESSION_DATES_SALES_START_NOT_BEFORE_SALES_END", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_SALES_START_NOT_BEFORE_SALES_END);
        ERROR_CODES.put("INVALID_SESSION_DATES_SALES_END_REQUIRED", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_SALES_END_REQUIRED);
        ERROR_CODES.put("INVALID_SESSION_DATES_SALES_END_BEFORE_BOOKING_END", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_SALES_END_BEFORE_BOOKING_END);
        ERROR_CODES.put("INVALID_SESSION_DATES_BOOKING_START_REQUIRED", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_REQUIRED);
        ERROR_CODES.put("INVALID_SESSION_DATES_BOOKING_START_BEFORE_RELEASE", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_BEFORE_RELEASE);
        ERROR_CODES.put("INVALID_SESSION_DATES_BOOKING_START_NOT_BEFORE_BOOKING_END", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_NOT_BEFORE_BOOKING_END);
        ERROR_CODES.put("INVALID_SESSION_DATES_BOOKING_END_REQUIRED", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_REQUIRED);
        ERROR_CODES.put("INVALID_SESSION_DATES_BOOKING_END_AFTER_SALES_END", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_AFTER_SALES_END);
        ERROR_CODES.put("INVALID_SESSION_DATES_BOOKING_END_CONFLICT_EVENT_LIMIT", ApiMgmtSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_CONFLICT_EVENT_LIMIT);

        ERROR_CODES.put("INVENTORY_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_INVENTORY_NOT_FOUND);
        ERROR_CODES.put("INVENTORY_LIST_ERROR", ApiMgmtErrorCode.EXTERNAL_INVENTORY_LIST_ERROR);
        ERROR_CODES.put("INVENTORY_ID_MANDATORY", ApiMgmtErrorCode.INVENTORY_ID_MANDATORY);
        ERROR_CODES.put("ST_INVENTORY_ID_MANDATORY", ApiMgmtErrorCode.ST_INVENTORY_ID_MANDATORY);
        ERROR_CODES.put("INVENTORY_IMPORT_ERROR", ApiMgmtErrorCode.INVENTORY_IMPORT_ERROR);
        ERROR_CODES.put("INVENTORY_UPDATE_ERROR", ApiMgmtErrorCode.INVENTORY_UPDATE_ERROR);

        ERROR_CODES.put("VENUE_TEMPLATE_ALREADY_EXISTS", ApiMgmtErrorCode.VENUE_TEMPLATE_ALREADY_EXISTS);
        ERROR_CODES.put("VENUE_TEMPLATE_MAPPING_NOT_FOUND", ApiMgmtErrorCode.VENUE_TEMPLATE_MAPPING_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_ID_MANDATORY",ApiMgmtErrorCode.VENUE_TEMPLATE_ID_MANDATORY);
        ERROR_CODES.put("ST_VENUE_TEMPLATE_ID_MANDATORY",ApiMgmtErrorCode.ST_VENUE_TEMPLATE_ID_MANDATORY);
        ERROR_CODES.put("VENUE_TEMPLATE_NOT_FOUND",ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_INVALID_SPACE",ApiMgmtErrorCode.VENUE_TEMPLATE_INVALID_SPACE);
        ERROR_CODES.put("VENUE_TEMPLATE_SVG_GENERATION_ERROR",ApiMgmtErrorCode.VENUE_TEMPLATE_SVG_GENERATION_ERROR);
        ERROR_CODES.put("VENUE_TEMPLATE_SEATS_CREATION_ERROR",ApiMgmtErrorCode.VENUE_TEMPLATE_SEATS_CREATION_ERROR);

        ERROR_CODES.put("PRICE_TYPE_CREATION_ERROR",ApiMgmtErrorCode.PRICE_TYPE_CREATION_ERROR);
        ERROR_CODES.put("PRICE_TYPE_NOT_FOUND",ApiMgmtErrorCode.PRICE_TYPE_NOT_FOUND);

        ERROR_CODES.put("SEAT_MAPPING_CONFLICT",ApiMgmtErrorCode.SEAT_MAPPING_CONFLICT);

        ERROR_CODES.put("RATE_MAPPING_NOT_FOUND",ApiMgmtErrorCode.RATE_CONFLICT);
        ERROR_CODES.put("DEFAULT_RATE_NOT_FOUND",ApiMgmtErrorCode.RATE_CONFLICT);
        ERROR_CODES.put("INVALID_RATE_REQUEST",ApiMgmtErrorCode.RATE_CONFLICT);

        ERROR_CODES.put("EXTERNAL_SESSION_ID_MANDATORY", ApiMgmtErrorCode.EXTERNAL_SESSION_ID_MANDATORY);
        ERROR_CODES.put("EXTERNAL_SESSION_NOT_RELATED_TO_EXTERNAL_EVENT", ApiMgmtErrorCode.EXTERNAL_SESSION_NOT_RELATED_TO_EXTERNAL_EVENT);
        ERROR_CODES.put("EXTERNAL_SESSION_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_SESSION_NOT_FOUND);
        ERROR_CODES.put("EXTERNAL_VENUE_ID_ERROR", ApiMgmtErrorCode.EXTERNAL_VENUE_ID_ERROR);
        ERROR_CODES.put("EXTERNAL_VENUE_TEMPLATE_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_VENUE_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("EXTERNAL_VENUE_TEMPLATE_PROCESSING_ERROR", ApiMgmtErrorCode.EXTERNAL_VENUE_TEMPLATE_PROCESSING_ERROR);
        ERROR_CODES.put("EXTERNAL_EVENT_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_EVENT_NOT_FOUND);
        ERROR_CODES.put("EXTERNAL_ST_EVENT_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_ST_EVENT_NOT_FOUND);
        ERROR_CODES.put("PRODUCT_NOT_FOUND", ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        ERROR_CODES.put("EXTERNAL_RATES_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_RATES_NOT_FOUND);
        ERROR_CODES.put("REPEATED_NAME", ApiMgmtErrorCode.REPEATED_NAME);
        ERROR_CODES.put("SGA_NOT_FOUND", ApiMgmtErrorCode.SGA_NOT_FOUND);
        ERROR_CODES.put("INVALID_VENUE_TEMPLATE", ApiMgmtErrorCode.INVALID_VENUE_TEMPLATE);
        ERROR_CODES.put("PRODUCT_CONFIGURATIONS_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_PRODUCT_CONFIGURATION_NOT_FOUND);
        ERROR_CODES.put("EVENT_RATES_NOT_FOUND", ApiMgmtErrorCode.RATE_CONFLICT);
        ERROR_CODES.put("EXTERNAL_PRESALE_ID_MANDATORY", ApiMgmtErrorCode.EXTERNAL_PRESALE_ID_MANDATORY);
        ERROR_CODES.put("EXTERNAL_PRESALE_ID_ALREADY_EXISTS", ApiMgmtErrorCode.EXTERNAL_PRESALE_ID_ALREADY_EXISTS);
        ERROR_CODES.put("EXTERNAL_PRESALE_NOT_FOUND", ApiMgmtErrorCode.EXTERNAL_PRESALE_NOT_FOUND);
        ERROR_CODES.put("CUSTOMER_TYPE_NOT_FOUND", ApiMgmtErrorCode.CUSTOMER_TYPE_NOT_FOUND);
        ERROR_CODES.put("SESSION_NOT_FOUND", ApiMgmtErrorCode.SESSION_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_NAME_CONFLICT", ApiMgmtErrorCode.VENUE_TEMPLATE_NAME_CONFLICT);
    }

    @Autowired
    public IntDispatcherDatasource(@Value("${clients.services.int-dispatcher-service}") String baseUrl,
                                   ObjectMapper jacksonMapper,
                                   Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new IntegrationHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(TIMEOUT)
                .build();
    }

    public TermInfoList getTermsInfo(Long entityId) {

        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(QUERY_PARAM_VENUE_ID_UNDERSCORE, entityId).build();
        return httpClient.buildRequest(HttpMethod.GET, GET_TERMS_INFO)
                .pathParams(entityId)
                .headers(headers)
                .params(params)
                .execute(TermInfoList.class);
    }

    public RolInfoList getRolesInfo(Long entityId, Long capacityId) {

        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(QUERY_PARAM_VENUE_ID_UNDERSCORE, entityId)
                .addQueryParameter("capacity_id", capacityId).build();

        return httpClient.buildRequest(HttpMethod.GET, GET_ROLES_INFO)
                .pathParams(entityId, capacityId)
                .headers(headers)
                .params(params)
                .execute(RolInfoList.class);
    }

    public AforosList getAforosInfo(Long entityId) {

        return httpClient.buildRequest(HttpMethod.GET, GET_AFOROS_INFO)
                .pathParams(entityId)
                .headers(headers)
                .execute(AforosList.class);
    }

    public InventoriesList getExternalInventories(Long entityId) {

        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_INVENTORY)
                .pathParams(entityId)
                .headers(headers)
                .execute(InventoriesList.class);
    }

    public ExternalEventBaseList getExternalEvents(Long entityId, Long venueTemplateId, ExternalEventsProviderType type) {

        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter(QUERY_PARAM_VENUE_TEMPLATE_ID, venueTemplateId);
        if (type != null) {
            params.addQueryParameter(QUERY_PARAM_TYPE, type.name());
        }

        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_EVENTS)
                .pathParams(entityId)
                .params(params.build())
                .headers(headers)
                .execute(ExternalEventBaseList.class);
    }

    public ExternalSessionBaseList getExternalSessions(Long entityId, Long eventId, ExternalSessionStatus status) {

        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(QUERY_PARAM_EVENT_ID, eventId)
                .addQueryParameter(QUERY_PARAM_STATUS, status)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_SESSIONS)
                .pathParams(entityId, eventId)
                .params(params)
                .headers(headers)
                .execute(ExternalSessionBaseList.class);
    }

    public StatusDTO getConnectionStatus(Long entityId, ConnectionType connectionType) {

        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(QUERY_PARAM_ENTITY_ID, entityId).build();

        return httpClient.buildRequest(HttpMethod.GET, GET_CONNECTION_STATUS)
                .pathParams(connectionType.name())
                .headers(headers)
                .params(params)
                .execute(StatusDTO.class);
    }

    public Long createVenueTemplate(CreateVenueTemplateRequest venueTemplateRequest) {

        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE)
                .headers(headers)
                .pathParams(venueTemplateRequest.getEntityId())
                .body(new ClientRequestBody(venueTemplateRequest))
                .execute(IdDTO.class)
                .getId();
    }

    public void deleteVenueTemplate(Long entityId, Long venueTemplateId, UpdateVenueTemplate venueTemplate) {

        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_BY_ID)
                .headers(headers)
                .pathParams(entityId, venueTemplateId)
                .body(new ClientRequestBody(venueTemplate))
                .execute();
    }

    public Long createEvent(CreateEventData eventData) {

        return httpClient.buildRequest(HttpMethod.POST, EXTERNAL_EVENTS)
                .headers(headers)
                .pathParams(eventData.getEntityId())
                .body(new ClientRequestBody(eventData))
                .execute(IdDTO.class)
                .getId();
    }

    public void deleteEvent(Long entityId, Event updateEvent) {

        httpClient.buildRequest(HttpMethod.DELETE, EXTERNAL_EVENT_ID)
                .headers(headers)
                .pathParams(entityId, updateEvent.getId())
                .execute();
    }

    public Long createSeasonTicket(CreateSeasonTicketData createSeasonTicketData) {

        return httpClient.buildRequest(HttpMethod.POST, EXTERNAL_SEASONTICKETS)
                .headers(headers)
                .pathParams(createSeasonTicketData.getEntityId())
                .body(new ClientRequestBody(createSeasonTicketData))
                .execute(IdDTO.class)
                .getId();
    }

    public void deleteSeasonTicket(Long entityId, SeasonTicket seasonTicket) {

        httpClient.buildRequest(HttpMethod.DELETE, EXTERNAL_SEASONTICKET_ID)
                .headers(headers)
                .pathParams(entityId, seasonTicket.getId())
                .execute();
    }

    public Long createSession(Long eventId, CreateSessionData session) {
        IdDTO result = httpClient.buildRequest(HttpMethod.POST, EXTERNAL_SESSIONS)
            .headers(headers)
            .pathParams(session.getEntityId(), eventId)
            .body(new ClientRequestBody(session))
                .execute(IdDTO.class);
        return result != null ? result.getId() : null;
    }

    public void deleteSession(Long eventId, Session updateSession) {

        httpClient.buildRequest(HttpMethod.DELETE, EXTERNAL_SESSION_ID)
                .headers(headers)
                .pathParams(updateSession.getEntityId(), eventId, updateSession.getId())
                .execute();
    }

    public void updateSessionInventory(Long entityId, Long eventId, Long sessionId, Boolean isSmartBooking) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (BooleanUtils.isTrue(isSmartBooking)) {
            params.addQueryParameter("integrationType", IntegrationType.SMART_BOOKING);
        }
        params.addQueryParameter("items", List.of(UpdateInventoryType.PRICES, UpdateInventoryType.AVAILABILITY, UpdateInventoryType.SESSION_INFO));

        httpClient.buildRequest(HttpMethod.PUT, EXTERNAL_SESSION_INVENTORY)
                .headers(headers)
                .pathParams(entityId, eventId, sessionId)
                .params(params.build())
                .execute();
    }

    public void updateActivityInventory(Long entityId, Long eventId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("items", List.of(UpdateInventoryType.MEMBERSHIP_PRICES));

        httpClient.buildRequest(HttpMethod.PUT, EXTERNAL_EVENT_INVENTORY)
                .headers(headers)
                .pathParams(entityId, eventId)
                .params(params.build())
                .execute();
    }

    public void updateSeasonTicketInventory(Long entityId, Long seasonTicketId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("items", List.of(UpdateInventoryType.PRICES, UpdateInventoryType.AVAILABILITY, UpdateInventoryType.SESSION_INFO));

        httpClient.buildRequest(HttpMethod.PUT, EXTERNAL_SEASONTICKET_INVENTORY)
                .headers(headers)
                .pathParams(entityId, seasonTicketId)
                .params(params.build())
                .execute();
    }

    public MotivoEmisionSummary getEmissionReasons(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, GET_EMISSION_REASONS)
                .pathParams(entityId)
                .headers(headers)
                .execute(MotivoEmisionSummary.class);
    }

    public PaymentModes getPaymentModes(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, GET_PAYMENT_MODES)
                .pathParams(entityId)
                .headers(headers)
                .execute(PaymentModes.class);
    }

    public ExternalPresaleBaseList getExternalPresales(Long entityId, Long eventId, Long sessionId, boolean skipUsed) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("skip_used", skipUsed)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_PRESALES)
                .pathParams(entityId, eventId, sessionId)
                .params(params)
                .headers(headers)
                .execute(ExternalPresaleBaseList.class);
    }

    public PreSaleConfigDTO createPresale(Long eventId, Long sessionId, PreSaleConfigDTO body, boolean isSeason) {
        Long entityId = body.getEntityId();
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(QUERY_PARAM_ST, isSeason)
                .build();
        return httpClient.buildRequest(HttpMethod.POST, EXTERNAL_PRESALES)
                .pathParams(entityId, eventId, sessionId)
                .params(params)
                .headers(headers)
                .body(new ClientRequestBody(body))
                .execute(PreSaleConfigDTO.class);
    }

    public void deletePresale(Long entityId, Long eventId, Long sessionId, Long presaleId, boolean isSeason) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(QUERY_PARAM_ST, isSeason)
                .build();
        httpClient.buildRequest(HttpMethod.DELETE, EXTERNAL_PRESALE_ID)
                .pathParams(entityId, eventId, sessionId, presaleId)
                .params(params)
                .headers(headers)
                .execute();
    }

    public ExternalPresaleBaseList getExternalSeasonTicketPresales(Long entityId, Long seasonTicketId, boolean skipUsed) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("skip_used", skipUsed)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_SEASONTICKET_ID + "/presales")
                .pathParams(entityId, seasonTicketId)
                .params(params)
                .headers(headers)
                .execute(ExternalPresaleBaseList.class);
    }
}
