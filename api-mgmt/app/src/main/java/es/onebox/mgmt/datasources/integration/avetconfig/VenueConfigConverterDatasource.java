package es.onebox.mgmt.datasources.integration.avetconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.sessions.dto.IntegrationEventEntityDTO;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VenueConfigConverterDatasource {
    private static final int TIMEOUT = 60000;

    private static final String INTEGRATION_EVENTS = "/integration-events";
    private static final String INTEGRATION_MAPPINGS = "/mapping";
    private static final String SESSION_ID = "idSession";
    private static final String VENUE_TEMPLATE_ID = "idPlantilla";
    private static final String CAPACITY_ID = "capacityId";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    @Autowired
    public VenueConfigConverterDatasource(@Value("${clients.services.int-avet-venueconfig-converter}") String baseUrl,
                                          ObjectMapper jacksonMapper,
                                          TracingInterceptor tracingInterceptor) {


        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public IntegrationEventEntityDTO getIntegrationEvents(Long sessionId) {
        try {
            return httpClient.buildRequest(HttpMethod.GET, INTEGRATION_EVENTS + "/{eventId}")
                    .pathParams(sessionId)
                    .execute(IntegrationEventEntityDTO.class);
        } catch (Exception e) {
            throw new OneboxRestException(e);
        }
    }

    public void createTemplateMappings(Long venueTemplateId) {
        try {
            httpClient.buildRequest(HttpMethod.POST, INTEGRATION_MAPPINGS + "/member-full?idPlantilla=" + venueTemplateId)
                    .execute(IntegrationEventEntityDTO.class);
        } catch (Exception e) {
            throw new OneboxRestException(e);
        }
    }

    public void createSessionsTicketsMappings(Long entityId, Integer capacityId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(CAPACITY_ID, capacityId)
                .build();
        try {
            httpClient.buildRequest(HttpMethod.POST, INTEGRATION_MAPPINGS + "/{entityId}/ticket")
                    .params(params)
                    .pathParams(entityId)
                    .execute();
        } catch (Exception e){
            throw new OneboxRestException(e);
        }
    }

    public void createSessionMappingsFull(Long entityId, Integer capacityId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(CAPACITY_ID, capacityId)
                .build();
        try {
            httpClient.buildRequest(HttpMethod.POST, INTEGRATION_MAPPINGS + "/{entityId}/full")
                    .params(params)
                    .pathParams(entityId)
                    .execute();
        } catch (Exception e){
            throw new OneboxRestException(e);
        }
    }

    public void createSessionTicketsMappings(Long sessionId, Long venueTemplateId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(SESSION_ID, sessionId)
                .addQueryParameter(VENUE_TEMPLATE_ID, venueTemplateId)
                .build();
        try {
            httpClient.buildRequest(HttpMethod.POST, INTEGRATION_MAPPINGS + "/ticket")
                    .params(params)
                    .execute();
        } catch (Exception e){
            throw new OneboxRestException(e);
        }
    }

    public void createSessionMappingFull (Long sessionId, Long venueTemplateId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(SESSION_ID, sessionId)
                .addQueryParameter(VENUE_TEMPLATE_ID, venueTemplateId)
                .build();
        try {
            httpClient.buildRequest(HttpMethod.POST, INTEGRATION_MAPPINGS + "/full")
                    .params(params)
                    .execute();
        } catch (Exception e){
            throw new OneboxRestException(e);
        }
    }

}
