package es.onebox.common.datasources.avetconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.avetconfig.dto.CapacityDTO;
import es.onebox.common.datasources.avetconfig.dto.ClubConfig;
import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.exception.ApiExceptionBuilder;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IntAvetConfigDatasource {

    private final HttpClient httpClient;
    private static final String API_VERSION_V1 = "/avet-config/v1";
    private static final String API_VERSION_1_0 = "/avetconfig/1.0";
    private static final String CLUB_CONF_PATH = "/entities/{entityId}";
    private static final String SESSION_CONF_PATH = "/sessions/{sessionId}";
    private static final String ENTITY_CAPACITIES_PATH = CLUB_CONF_PATH + "/capacities";

    private static final int TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final Map<String, ApiExternalErrorCode> ERROR_CODES;

    static {
        ERROR_CODES = new HashMap<>();
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("GENERIC_ERROR", ApiExternalErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("ENTITY_ID_MANDATORY", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
    }

    @Autowired
    public IntAvetConfigDatasource(@Value("${clients.services.int-avet-config}") String baseUrl,
                                   ObjectMapper jacksonMapper,
                                   TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ApiExceptionBuilder(ERROR_CODES, jacksonMapper))
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(TIMEOUT)
                .build();
    }

    public ClubConfig getClubByEntityId(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, API_VERSION_1_0 + CLUB_CONF_PATH).pathParams(entityId).execute(ClubConfig.class);
    }

    public SessionMatch getSession(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, API_VERSION_V1 + SESSION_CONF_PATH).pathParams(sessionId).execute(SessionMatch.class);
    }

    public List<CapacityDTO> getEntityCapacities(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, API_VERSION_V1 + ENTITY_CAPACITIES_PATH)
                .pathParams(entityId)
                .execute(ListType.of(CapacityDTO.class));
    }

}
