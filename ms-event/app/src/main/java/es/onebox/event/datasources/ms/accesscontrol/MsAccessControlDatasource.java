package es.onebox.event.datasources.ms.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.event.datasources.ms.accesscontrol.dto.enums.AccessControlSystem;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsAccessControlDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/access-control-api/" + API_VERSION;
    private static final int TIMEOUT = 60000;

    private static final String SYSTEMS = "/systems";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    @Autowired
    public MsAccessControlDatasource(@Value("${clients.services.ms-access-control}") String baseUrl,
                                     ObjectMapper jacksonMapper,
                                     TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public List<AccessControlSystem> getSystems(Long entityId, Long venueId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("entityId", entityId);
        builder.addQueryParameter("venueId", venueId);
        return httpClient.buildRequest(HttpMethod.GET, SYSTEMS)
                .params(builder.build())
                .execute(ListType.of(AccessControlSystem.class));
    }

}
