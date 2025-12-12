package es.onebox.common.datasources.ms.collective;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.collective.dto.ResponseCollectiveDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsCollectiveDatasource {

    private static final int TIMEOUT = 60000;

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-collective-api/" + API_VERSION;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("COLLECTIVE_NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsCollectiveDatasource(@Value("${clients.services.ms-collective}") String baseUrl,
                                  ObjectMapper jacksonMapper,
                                  TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .readTimeout(TIMEOUT)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public ResponseCollectiveDTO getCollective(Long collectiveId) {
        return httpClient.buildRequest(HttpMethod.GET, "/collectives/{id}")
                .pathParams(collectiveId)
                .execute(ResponseCollectiveDTO.class);
    }
}
