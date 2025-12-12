package es.onebox.common.datasources.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.oauth2.dto.TokenRequest;
import es.onebox.common.datasources.oauth2.dto.TokenResponse;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiOAuth2Datasource {

    private final HttpClient httpClient;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    @Autowired
    public ApiOAuth2Datasource(@Value("${clients.services.api-oauth2}") String baseUrl,
                               ObjectMapper jacksonMapper) {
        ObjectMapper mapper = JsonMapper.jacksonMapper();
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(mapper)
                .connectTimeout(5000)
                .readTimeout(10000)
//                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public TokenResponse getToken(TokenRequest tokenRequest) {
        ClientRequestBody body = new ClientRequestBody(tokenRequest, ClientRequestBody.Type.FORM);
        return httpClient.buildRequest(HttpMethod.POST, "/oauth/token")
                .body(body)
                .execute(TokenResponse.class);
    }

}
