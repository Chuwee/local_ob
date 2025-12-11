package es.onebox.event.datasources.integration.avet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.event.datasources.integration.avet.config.dto.AvetPrice;
import es.onebox.event.datasources.integration.avet.config.dto.ClubConfig;
import es.onebox.event.datasources.integration.avet.config.dto.Competition;
import es.onebox.event.datasources.integration.avet.config.dto.SessionMatch;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.event.exception.MsEventErrorCode;
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
    private static final String BASE_PATH_V1 = "/avet-config/v1";
    private static final String BASE_PATH_1DOT0 = "/avetconfig/1.0";
    private static final String AVET_EVENT_PRICES = BASE_PATH_V1 + "/event/{eventId}/prices";
    private static final String AVET_CLUB_CONFIG = BASE_PATH_1DOT0 + "/entities/{entityId}";
    private static final String AVET_SESSION = BASE_PATH_V1 + "/sessions/{sessionId}";
    private static final String AVET_COMPETITION = BASE_PATH_1DOT0 + "/competitions/{competitionId}";
    private static final Map<String, ErrorCode> ERROR_CODES;

    static {
        ERROR_CODES = new HashMap<>();
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", MsEventErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("GENERIC_ERROR", MsEventErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("ENTITY_ID_MANDATORY", MsEventErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("COMPETITION_NOT_FOUND", MsEventErrorCode.COMPETITION_NOT_FOUND);
        ERROR_CODES.put("EVENT_ID_MANDATORY", MsEventErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("COMPETITION_ID_MANDATORY", MsEventErrorCode.BAD_REQUEST_PARAMETER);
    }

    @Autowired
    public IntAvetConfigDatasource(@Value("${clients.services.int-avet-config}") String baseUrl,
                                   ObjectMapper jacksonMapper,
                                   TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public List<AvetPrice> getAvetPrices(Integer eventId) {
        return httpClient.buildRequest(HttpMethod.GET, AVET_EVENT_PRICES)
                .pathParams(eventId)
                .execute(ListType.of(AvetPrice.class));
    }

    public ClubConfig getClubConfig(Integer entityId) {
        return httpClient.buildRequest(HttpMethod.GET, AVET_CLUB_CONFIG)
                .pathParams(entityId)
                .execute(ClubConfig.class);
    }

    public SessionMatch getSessionMatch(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, AVET_SESSION)
                .pathParams(sessionId)
                .execute(SessionMatch.class);
    }

    public Competition getCompetition(Long competitionId) {
        return httpClient.buildRequest(HttpMethod.GET, AVET_COMPETITION)
                .pathParams(competitionId)
                .execute(Competition.class);
    }
}
