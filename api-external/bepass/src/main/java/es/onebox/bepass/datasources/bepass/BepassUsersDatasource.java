package es.onebox.bepass.datasources.bepass;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.bepass.datasources.bepass.config.BepassConfig;
import es.onebox.bepass.datasources.bepass.dto.CreateUserRequest;
import es.onebox.bepass.datasources.bepass.dto.UserValidationResponse;
import es.onebox.bepass.exception.BepassErrorCode;
import es.onebox.bepass.exception.BepassHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.HttpResponse;
import es.onebox.datasource.http.response.HttpResponseBodyType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BepassUsersDatasource extends BepassDatasource {

    private static final String USER = "/user";
    private static final String HEADER_X_ID = "x-id";
    private static final String VALIDATE_BY_USER_ID = "/biometry/validate";
    private static final String VALIDATE_BY_DOCUMENT = "/biometry/validate/byDocument";

    private final HttpClient httpClient;
    private static final Map<String, BepassErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("not found", BepassErrorCode.USER_NOT_FOUND);
        ERROR_CODES.put("phoneNumber must be a valid phone number", BepassErrorCode.INVALID_PHONE_NUMBER);
        ERROR_CODES.put("phoneNumber", BepassErrorCode.INVALID_PHONE_NUMBER);
        ERROR_CODES.put("originCompanyId", BepassErrorCode.INVALID_COMPANY_ID);
        ERROR_CODES.put("validation_failed", BepassErrorCode.INVALID_FIELD);
        ERROR_CODES.put("internal_error", BepassErrorCode.BEPASS_GENERIC_ERROR);
    }

    public BepassUsersDatasource(BepassConfig config,
                                 TracingInterceptor bepassTracingInterceptor,
                                 ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .baseUrl(config.getUsers().getUrl())
                .interceptors(bepassTracingInterceptor)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new BepassHttpExceptionBuilder(jacksonMapper, ERROR_CODES))
                .build();
    }

    public HttpResponse createUser(String token, CreateUserRequest in) {
        return httpClient.buildRequest(HttpMethod.POST, USER)
                .headers(prepareHeaders(token))
                .body(new ClientRequestBody(in))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
    }

    public UserValidationResponse validateByDocument(String token, String document) {
        return httpClient.buildRequest(HttpMethod.GET, VALIDATE_BY_DOCUMENT)
                .headers(this.prepareHeaders(token, document))
                .execute(UserValidationResponse.class);
    }

    public UserValidationResponse validateByUserId(String token, String bepassToken) {
        return httpClient.buildRequest(HttpMethod.GET, VALIDATE_BY_USER_ID)
                .headers(this.prepareHeaders(token, bepassToken))
                .execute(UserValidationResponse.class);
    }


    protected RequestHeaders prepareHeaders(String token, String xId) {
        return new RequestHeaders.Builder()
                .addHeader(HEADER_X_ID, xId)
                .addHeader(HEADER_API_KEY, token)
                .addHeader(HEADER_TENANT_ID, BepassAuthContext.get().tenantId())
                .build();
    }
}
