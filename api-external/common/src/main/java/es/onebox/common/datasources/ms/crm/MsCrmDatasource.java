package es.onebox.common.datasources.ms.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.crm.dto.CrmClientResponse;
import es.onebox.common.datasources.ms.crm.dto.CrmOrderParams;
import es.onebox.common.datasources.ms.crm.dto.CrmOrderResponse;
import es.onebox.common.datasources.ms.crm.dto.CrmParams;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
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
public class MsCrmDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-crm-api/" + API_VERSION;
    private static final String AUDITCRM = "/auditcrm";
    private static final String ORDERS = "/orders";
    private static final String BUYERS = "/buyers";

    private final HttpClient httpClient;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("400G0001", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("SUBSCRIPTION_LIST_NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
    }

    @Autowired
    public MsCrmDatasource(@Value("${clients.services.ms-crm}") String baseUrl,
                           ObjectMapper jacksonMapper,
                           TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public CrmOrderResponse getAbandonedOrder(CrmOrderParams filter) {
        return httpClient.buildRequest(HttpMethod.POST, AUDITCRM + ORDERS)
                .body(new ClientRequestBody(filter))
                .execute(CrmOrderResponse.class);
    }

    public CrmClientResponse getAbandonedClient(CrmParams filter) {
        return httpClient.buildRequest(HttpMethod.POST, AUDITCRM + BUYERS)
                .body(new ClientRequestBody(filter))
                .execute(CrmClientResponse.class);
    }

}
