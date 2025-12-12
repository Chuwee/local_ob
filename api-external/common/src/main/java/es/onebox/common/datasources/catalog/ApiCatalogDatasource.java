package es.onebox.common.datasources.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventsResponse;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.ChannelSessionResponse;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionAvailabilityResponse;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionVenueMapResponse;
import es.onebox.common.datasources.catalog.dto.session.prices.SessionPrices;
import es.onebox.common.datasources.catalog.dto.session.request.EventsRequestDTO;
import es.onebox.common.datasources.catalog.dto.session.request.SessionsRequestDTO;
import es.onebox.common.exception.ApiExceptionBuilder;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApiCatalogDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/catalog-api/" + API_VERSION;

    private static final String EVENTS = "/events";
    private static final String SESSIONS = "/sessions";
    private static final String AVAILABILITY = "/availability";
    private static final String PRICES = "/prices";
    private static final String VENUE_MAP = "/venue-map";

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String SESSIONS_START = "sessions_start";
    private static final String START = "start";
    private static final String VENUE_ID = "venue_id";

    private static final long CONNECTION_TIMEOUT = 10000L;

    private final HttpClient httpClient;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("ACAT0000", ApiExternalErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("ACAT0001", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("ACAT0002", ApiExternalErrorCode.REQUEST_BODY_INCORRECT);
        ERROR_CODES.put("ACAT0003", ApiExternalErrorCode.UNSUPPORTED_MEDIA_TYPE);
        ERROR_CODES.put("ACAT0004", ApiExternalErrorCode.INVALID_PARAM_FORMAT);
        ERROR_CODES.put("ACAT0005", ApiExternalErrorCode.REQUEST_PARAM_SIZE_TOO_LARGE);
        ERROR_CODES.put("ACAT0006", ApiExternalErrorCode.INVALID_TIME_ZONE);
        ERROR_CODES.put("ACAT0007", ApiExternalErrorCode.INVALID_TOKEN);
        ERROR_CODES.put("ACAT0008", ApiExternalErrorCode.CHANNEL_ID_REQUIRED);
        ERROR_CODES.put("ACAT0009", ApiExternalErrorCode.EVENT_NOT_FOUND);
        ERROR_CODES.put("ACAT0010", ApiExternalErrorCode.SESSION_NOT_FOUND);
        ERROR_CODES.put("NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("INVALID_PARAM", ApiExternalErrorCode.INVALID_PARAM);
    }

    @Autowired
    public ApiCatalogDatasource(@Value("${clients.services.api-catalog}") String baseUrl) {
        ObjectMapper mapper = JsonMapper.jacksonMapper();
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(mapper)
                .exceptionBuilder(new ApiExceptionBuilder(ERROR_CODES, mapper))
                .build();
    }

    public ChannelEventsResponse getEvents(String token, Long limit, Long offset, ZonedDateTime gte, ZonedDateTime lte, Long locationId) {

        Map<Operator, ZonedDateTime> dates = new HashMap<>();
        if (gte != null) {
            dates.put(Operator.GREATER_THAN_OR_EQUALS, gte);
        }
        if (lte != null) {
            dates.put(Operator.LESS_THAN_OR_EQUALS, lte);
        }

        Map<String, Object> params = new HashMap<>();
        params.put(LIMIT, limit);
        params.put(OFFSET, offset);
        if (!dates.isEmpty()) {
            params.put(SESSIONS_START, DatasourceUtils.prepareDateFilters(dates));
        }
        if (locationId != null) {
            params.put(VENUE_ID, locationId);
        }

        return httpClient.buildRequest(HttpMethod.GET, EVENTS)
                .params(DatasourceUtils.prepareQueryParams(params))
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelEventsResponse.class);
    }

    public ChannelEventDetail getEvent(String token, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + "/{eventId}")
                .pathParams(eventId)
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelEventDetail.class);
    }

    public ChannelSession getSession(String token, Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + "/{eventId}" + SESSIONS + "/{sessionId}")
                .pathParams(eventId, sessionId)
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelSession.class);
    }

    public ChannelSessionAvailabilityResponse getSessionAvailability(String token, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSIONS + "/{sessionId}" + AVAILABILITY)
                .pathParams(sessionId)
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelSessionAvailabilityResponse.class);
    }

    public ChannelSessionVenueMapResponse getSessionVenueMap(String token, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSIONS + "/{sessionId}" + VENUE_MAP)
                .pathParams(sessionId)
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelSessionVenueMapResponse.class);
    }

    public SessionPrices getSessionPrices(String token, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSIONS + "/{sessionId}" + PRICES)
                .pathParams(sessionId)
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(SessionPrices.class);
    }

    public ChannelSessionResponse getSessions(String token, Long eventId, Long limit, Long offset,
                                              ZonedDateTime gte, ZonedDateTime lte) {
        Map<Operator, ZonedDateTime> dates = new HashMap<>();
        if (gte != null) {
            dates.put(Operator.GREATER_THAN_OR_EQUALS, gte);
        }
        if (lte != null) {
            dates.put(Operator.LESS_THAN_OR_EQUALS, lte);
        }

        Map<String, Object> params = new HashMap<>();
        params.put(LIMIT, limit);
        params.put(OFFSET, offset);
        if (!dates.isEmpty()) {
            params.put(START, DatasourceUtils.prepareDateFilters(dates));
        }

        return httpClient.buildRequest(HttpMethod.GET, EVENTS + "/{eventId}" + SESSIONS)
                .pathParams(eventId)
                .params(DatasourceUtils.prepareQueryParams(params))
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelSessionResponse.class);
    }

    public ChannelSessionResponse getSessions(String token, SessionsRequestDTO request) {
        QueryParameters.Builder builder = new QueryParameters.Builder().addQueryParameters(request);
        return httpClient.buildRequest(HttpMethod.GET, "" + SESSIONS)
                .params(builder.build())
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelSessionResponse.class);
    }

    public ChannelSessionResponse getSessionsByNextPage(String token, String urlNextPage) {
        if (StringUtils.isBlank(urlNextPage)) {
            return null;
        }
        if (urlNextPage.contains(BASE_PATH)) {
            urlNextPage = urlNextPage.replace(BASE_PATH, "");
        }
        return httpClient.buildRequest(HttpMethod.GET, urlNextPage)
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelSessionResponse.class);
    }

    public ChannelEventsResponse getEvents(String token, EventsRequestDTO request) {
        QueryParameters.Builder builder = new QueryParameters.Builder().addQueryParameters(request);
        return httpClient.buildRequest(HttpMethod.GET, "" + EVENTS)
                .params(builder.build())
                .headers(DatasourceUtils.prepareAuthHeader(token))
                .execute(ChannelEventsResponse.class);
    }
}
