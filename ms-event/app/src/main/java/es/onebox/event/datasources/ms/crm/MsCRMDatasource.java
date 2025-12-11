package es.onebox.event.datasources.ms.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.event.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsCRMDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-crm-api/" + API_VERSION;

    private static final String ENTITIES = "/entities";
    private static final String ENTITY_ID = "/{entityId}";
    private static final String SUBSCRIPTION = "/subscriptions";
    private static final String SUBSCRIPTION_ID = "/{subscriptionId}";

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("SUBSCRIPTION_LIST_NOT_FOUND", MsEventSessionErrorCode.SUBSCRIPTION_LIST_ID_NOT_FOUND);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsCRMDatasource(@Value("${clients.services.ms-crm}") String baseUrl,
                           ObjectMapper jacksonMapper,
                           TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public SubscriptionDTO getSubscriptionList(Integer entityId, Integer subscriptionListId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + ENTITY_ID + SUBSCRIPTION + SUBSCRIPTION_ID)
                .pathParams(entityId, subscriptionListId)
                .execute(SubscriptionDTO.class);
    }
}
