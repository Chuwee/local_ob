package es.onebox.mgmt.datasources.ms.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.mgmt.entities.dto.SubscriptionRequestFilter;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsCrmDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-crm-api/" + API_VERSION;
    private static final String ENTITIES = "/entities";
    private static final String CLIENTS = "/clients";
    private static final String SUBSCRIPTION = "/subscriptions";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("400G0001", ApiMgmtErrorCode.SUBSCRIPTION_LIST_NOT_FOUND);
        ERROR_CODES.put("SUBSCRIPTION_LIST_NOT_FOUND", ApiMgmtErrorCode.SUBSCRIPTION_LIST_NOT_FOUND);
        ERROR_CODES.put("SUBSCRIPTION_CANNOT_BE_DEACTIVATED", ApiMgmtErrorCode.SUBSCRIPTION_CANNOT_BE_DEACTIVATED);
    }

    public static ErrorCode getErrorCode(String msEventErrorCode) {
        return ERROR_CODES.getOrDefault(msEventErrorCode, ApiMgmtErrorCode.GENERIC_ERROR);
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

    public List<SubscriptionDTO> getSubscriptionLists(SubscriptionRequestFilter filter, Long entityId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
            params.addQueryParameters(filter);

        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}" + CLIENTS + SUBSCRIPTION)
                .pathParams(entityId)
                .params(params.build())
                .execute(ListType.of(SubscriptionDTO.class));
    }

    public SubscriptionDTO getSubscriptionList(Long entityId, Long operatorId, Integer subscriptionListId, Long entityAdminId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (operatorId != null) {
            params.addQueryParameter("operatorId", operatorId);
        }
        if (entityAdminId != null) {
            params.addQueryParameter("entityAdminId", entityAdminId);
        }
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}" + SUBSCRIPTION + "/{subscriptionListId}")
                .pathParams(entityId, subscriptionListId)
                .params(params.build())
                .execute(SubscriptionDTO.class);
    }

    public SubscriptionDTO getSubscriptionList(Integer subscriptionListId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + SUBSCRIPTION + "/{subscriptionListId}")
                .pathParams(subscriptionListId)
                .execute(SubscriptionDTO.class);
    }

    public SubscriptionDTO addSubscriptionLists(Long entityId, SubscriptionDTO subscriptionDTO) {
        return httpClient.buildRequest(HttpMethod.POST, ENTITIES + "/{entityId}" + SUBSCRIPTION)
                .pathParams(entityId)
                .body(new ClientRequestBody(subscriptionDTO))
                .execute(SubscriptionDTO.class);
    }

    public void updateSubscriptionLists(Long entityId, Long subscriptionListId, SubscriptionDTO subscriptionDTO) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}" + SUBSCRIPTION + "/{subscriptionId}")
                .pathParams(entityId, subscriptionListId)
                .body(new ClientRequestBody(subscriptionDTO))
                .execute(SubscriptionDTO.class);
    }

    public void deleteSubscriptionLists(Long entityId, Long subscriptionListId) {
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setStatus((byte)0);
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}" + SUBSCRIPTION + "/{subscriptionId}")
                .pathParams(entityId, subscriptionListId)
                .body(new ClientRequestBody(subscriptionDTO))
                .execute(SubscriptionDTO.class);
    }

}
