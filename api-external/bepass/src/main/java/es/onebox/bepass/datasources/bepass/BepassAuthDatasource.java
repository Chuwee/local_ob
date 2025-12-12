package es.onebox.bepass.datasources.bepass;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.audit.okhttp.AuditTracingInterceptor;
import es.onebox.bepass.datasources.bepass.config.AuthConfig;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.bepass.datasources.bepass.config.BepassConfig;
import es.onebox.bepass.datasources.bepass.dto.AuthRequest;
import es.onebox.bepass.datasources.bepass.dto.AuthResponse;
import es.onebox.bepass.exception.BepassErrorCode;
import es.onebox.bepass.exception.BepassHttpExceptionBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BepassAuthDatasource extends BepassDatasource {

    private final HttpClient httpClient;

    private static final String AUTH = "/auth/generate";
    private final AuthRequest authRequest;

    private static final Map<String, BepassErrorCode> ERROR_CODES = new HashMap<>();

    static {

    }

    public BepassAuthDatasource(AuthConfig config,
                                AuditTracingInterceptor tracingInterceptor,
                                ObjectMapper jacksonMapper) {
        super();
        this.authRequest = new AuthRequest(config.getPassword(), config.getUsername());
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .interceptors(tracingInterceptor)
                .baseUrl(config.getUrl())
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new BepassHttpExceptionBuilder(jacksonMapper, ERROR_CODES))
                .build();
    }

    public AuthResponse getToken() {
        return httpClient.buildRequest(HttpMethod.POST, AUTH)
                .body(new ClientRequestBody(authRequest))
                .headers(prepareTenantHeader())
                .execute(AuthResponse.class);
    }

}
