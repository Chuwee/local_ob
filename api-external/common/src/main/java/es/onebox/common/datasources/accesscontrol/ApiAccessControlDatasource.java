package es.onebox.common.datasources.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.accesscontrol.dto.ACTicketResponse;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeListDTO;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeListFilter;
import es.onebox.common.datasources.accesscontrol.dto.TicketFilter;
import es.onebox.common.datasources.common.converters.ConvertUtils;
import es.onebox.common.datasources.ms.entity.enums.EntityField;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.exception.ApiExceptionBuilder;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiAccessControlDatasource {
    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/access-control-api/" + API_VERSION;

    private static final String TICKETS = "/tickets";
    private static final String WHITELIST = "/events/{eventId}/sessions/{sessionId}/whitelist";

    private final HttpClient httpClient;
    private static final long CONNECTION_TIMEOUT = 10000L;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();
    static {
        ERROR_CODES.put("AACC0000", ApiExternalErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("AACC0001", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("ACCESS_DENIED", ApiExternalErrorCode.ACCESS_DENIED);
        ERROR_CODES.put("0006", ApiExternalErrorCode.SESSION_NOT_FOUND);
    }

    @Autowired
    public ApiAccessControlDatasource(@Value("${clients.services.api-access-control}") String baseUrl,
                               ObjectMapper jacksonMapper, TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ApiExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public ACTicketResponse getTickets(TicketFilter filter, String token) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, TICKETS)
                .params(params.build())
                .headers(prepareAuthHeader(token))
                .execute(ACTicketResponse.class);
    }

    public BarcodeListDTO getWhitelist(String token, Long eventId, Long sessionId, BarcodeListFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, WHITELIST)
                .pathParams(eventId, sessionId)
                .params(params.build())
                .headers(prepareAuthHeader(token))
                .execute(BarcodeListDTO.class);
    }

    private RequestHeaders prepareAuthHeader(String token) {
        return new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
