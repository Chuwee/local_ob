package es.onebox.event.datasources.integration.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.event.datasources.integration.dispatcher.dto.ConnectorRelation;
import es.onebox.event.datasources.integration.dispatcher.dto.ExternalEvent;
import es.onebox.event.datasources.integration.dispatcher.dto.ExternalSession;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IntDispatcherServiceDatasource {

    private static final String CONNECTORS_RELATION = "/connectors-relation";
    private static final String API_VERSION = "/mgmt-api/v1";
    private static final String ENTITIES_ID = "/entities/{entityId}";
    private static final String EVENTS_ID = "/events/{eventId}";
    private static final String SESSIONS_ID = "/sessions/{sessionId}";
    private static final String SESSION_PATH = API_VERSION + ENTITIES_ID + EVENTS_ID + SESSIONS_ID;
    private static final String EVENT_PATH = API_VERSION + ENTITIES_ID + EVENTS_ID;
    private static final String EVENT_PUBLISH_PATH = EVENT_PATH + "/publish";
    private static final String PRESALES_ID = "/presales/{presaleId}";
    private static final String PRESALE_PATH = API_VERSION + ENTITIES_ID + EVENTS_ID + SESSIONS_ID + PRESALES_ID;
    private final HttpClient httpClient;
    private static final Map<String, ErrorCode> ERROR_CODES;

    static {
        ERROR_CODES = new HashMap<>();
        ERROR_CODES.put("EVENT_SESSION_MAPPING_NOT_FOUND", MsEventErrorCode.EVENT_SESSION_MAPPING_NOT_FOUND);
        ERROR_CODES.put("EVENT_MAPPING_NOT_FOUND", MsEventErrorCode.EVENT_MAPPING_NOT_FOUND);
        ERROR_CODES.put("SESSION_MAPPING_NOT_FOUND", MsEventErrorCode.EVENT_SESSION_MAPPING_NOT_FOUND);
    }

    @Autowired
    public IntDispatcherServiceDatasource(@Value("${clients.services.int-dispatcher-service}") String baseUrl,
                                          ObjectMapper jacksonMapper,
                                          TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public void createConnectorsRelation(ConnectorRelation connectorRelation) {
        httpClient.buildRequest(HttpMethod.POST, CONNECTORS_RELATION)
                .body(new ClientRequestBody(connectorRelation))
                .execute();
    }

    public void deleteExternalSession(Long entityId, Long eventId, Long sessionId) {
        httpClient.buildRequest(HttpMethod.DELETE, SESSION_PATH)
                .pathParams(entityId, eventId, sessionId)
                .execute();
    }

    public ExternalEvent getExternalEvent(Long entityId, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_PATH)
                .pathParams(entityId, eventId)
                .execute(ExternalEvent.class);
    }

    public ExternalSession getExternalSession(Long entityId, Long eventId, Long sessionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, SESSION_PATH)
                .pathParams(entityId, eventId, sessionId)
                .execute(ExternalSession.class);
    }

    public void publishEvent(Long entityId, Long eventId) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_PUBLISH_PATH)
                .pathParams(entityId, eventId)
                .execute();
    }

    public String getExternalPresale(Long entityId, Long eventId, Long sessionId, Long presaleId) {
        return httpClient.buildRequest(HttpMethod.GET, PRESALE_PATH)
                .pathParams(entityId, eventId, sessionId, presaleId)
                .execute(String.class);
    }

}
