package es.onebox.event.datasources.ms.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.event.datasources.ms.client.dto.ClientEntity;
import es.onebox.event.datasources.ms.client.dto.conditions.ClientConditionsDTO;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionsRequest;
import es.onebox.event.datasources.ms.client.dto.Customer;
import es.onebox.event.datasources.ms.client.dto.CustomerExternalProduct;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import okhttp3.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MsClientDatasource {

    private static final String PARAM_ENTITY_ID = "entityId";

    private static final String API_VERSION = "1.0";
    private static final String BASE_PATH = "/clients-api/" + API_VERSION;
    private static final String CUSTOMERS_PATH = "/customers";
    private static final String EXTERNAL_PRODUCTS_PATH = "/external-products";
    private static final int READ_TIMEOUT = 60000;
    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    private final HttpClient httpClient;

    @Autowired
    public MsClientDatasource(@Value("${clients.services.ms-client}") String baseUrl,
                              ObjectMapper jacksonMapper,
                              Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(READ_TIMEOUT)
                .build();
    }

    public List<CustomerExternalProduct> getExternalProductsFromExternalEvent(Integer entityId, String externalEventId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(PARAM_ENTITY_ID, entityId)
                .addQueryParameter("externalEventId", externalEventId)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, CUSTOMERS_PATH + EXTERNAL_PRODUCTS_PATH)
                .params(params)
                .execute(ListType.of(CustomerExternalProduct.class));
    }

    public Customer getCustomer(String customerId, Long entityId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(PARAM_ENTITY_ID, entityId)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, CUSTOMERS_PATH + "/{customerId}")
                .pathParams(customerId)
                .params(params)
                .execute(Customer.class);
    }

    public List<ClientEntity> getClientEntities(Long entityId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter(PARAM_ENTITY_ID, entityId);
        params.addQueryParameter("active", Boolean.TRUE);
        return this.httpClient.buildRequest(HttpMethod.GET, "/clientEntities")
                .params(params.build()).execute(ListType.of(ClientEntity.class));
    }

    public ClientConditionsDTO getConditions(ConditionsRequest params) {
        var queryParams = new QueryParameters.Builder().addQueryParameters(params).build();
        return httpClient.buildRequest(HttpMethod.GET, "/conditions")
                .params(queryParams)
                .execute(ClientConditionsDTO.class);
    }
}
