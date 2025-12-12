package es.onebox.common.datasources.ms.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.client.dto.AuthVendorChannelConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorUserData;
import es.onebox.common.datasources.ms.client.dto.Client;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.dto.CustomerTypeAutomaticAssignment;
import es.onebox.common.datasources.ms.client.dto.CustomerTypeProcessorResponse;
import es.onebox.common.datasources.ms.client.dto.request.AuthOrigin;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerRequest;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerResponse;
import es.onebox.common.datasources.ms.client.dto.request.CreateExternalCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.request.SearchCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.response.CustomersResponse;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MsClientDatasource {

    private static final String VENDORS_API_VERSION = "v1";
    private static final String VENDORS_BASE_PATH = "/vendors-api/" + VENDORS_API_VERSION;
    private static final String CLIENTS_API_VERSION = "1.0";
    private static final String CLIENTS_API_PATH = "/clients-api/" + CLIENTS_API_VERSION;
    private static final String CUSTOMER_ID = "/customers/{customerId}";


    private final HttpClient httpClient;
    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("400AV0001", ApiExternalErrorCode.CODE_EXPIRED);
        ERROR_CODES.put("400AV0002", ApiExternalErrorCode.INVALID_GRANT);
        ERROR_CODES.put("400AV0003", ApiExternalErrorCode.REQUIRED_CODE);
        ERROR_CODES.put("400AV0004", ApiExternalErrorCode.REQUIRED_GRANT_TYPE);
        ERROR_CODES.put("400AV0005", ApiExternalErrorCode.REQUIRED_REDIRECT_URI);
        ERROR_CODES.put("400AV0006", ApiExternalErrorCode.REQUIRED_CLIENT);
        ERROR_CODES.put("400AV0007", ApiExternalErrorCode.UNKNOWN_CLIENT);
        ERROR_CODES.put("400AV0008", ApiExternalErrorCode.INVALID_CREDENTIALS);
        ERROR_CODES.put("400AV0009", ApiExternalErrorCode.INVALID_CLIENT_ID);
        ERROR_CODES.put("400AV0010", ApiExternalErrorCode.INVALID_AUTH_OPTION);
        ERROR_CODES.put("404CL0023", ApiExternalErrorCode.USER_NOT_FOUND);
        ERROR_CODES.put("NOT_MEMBER_USERDATA", ApiExternalErrorCode.NOT_MEMBER_USERDATA);
        ERROR_CODES.put("INVALID_AUTHVENDOR_CONFIG", ApiExternalErrorCode.INVALID_AUTHVENDOR_CONFIG);
        ERROR_CODES.put("CLIENT_NOT_FOUND", ApiExternalErrorCode.CLIENT_NOT_FOUND);
        ERROR_CODES.put("ENTITY_ID_MANDATORY", ApiExternalErrorCode.ENTITY_ID_MANDATORY);
        ERROR_CODES.put("ENTITY_MODULE_B2B_DISABLED", ApiExternalErrorCode.ENTITY_MODULE_B2B_DISABLED);
    }

    @Autowired
    public MsClientDatasource(@Value("${clients.services.ms-client}") String baseUrl,
                              ObjectMapper jacksonMapper,
                              TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public List<Long> getAuthVendorChannels(String vendorId) {
        return httpClient.buildRequest(HttpMethod.GET, VENDORS_BASE_PATH + "/auth-vendors/{vendorId}/channels")
                .pathParams(vendorId)
                .execute(ListType.of(Long.class));
    }

    public AuthVendorConfig getAuthVendorConfiguration(String vendorId) {
        return httpClient.buildRequest(HttpMethod.GET, VENDORS_BASE_PATH + "/auth-vendors/{vendorId}")
                .pathParams(vendorId)
                .execute(AuthVendorConfig.class);
    }

    public AuthVendorUserData getUserData(final String vendorId, Map<String, Object> params) {
        return httpClient.buildRequest(HttpMethod.POST, VENDORS_BASE_PATH + "/auth-vendors/{id}/user-data")
                .pathParams(vendorId)
                .body(new ClientRequestBody(params))
                .execute(AuthVendorUserData.class);
    }

    public List<AuthVendorUserData> getRelatedUsers(final String vendorId, Map<String, Object> params) {
        return httpClient.buildRequest(HttpMethod.POST, VENDORS_BASE_PATH + "/auth-vendors/{id}/related-users")
                .pathParams(vendorId)
                .body(new ClientRequestBody(params))
                .execute(ListType.of(AuthVendorUserData.class));
    }

    public AuthVendorChannelConfig getAuthVendorChannelConfiguration(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, VENDORS_BASE_PATH + "/channels/{channelId}")
                .pathParams(channelId)
                .execute(AuthVendorChannelConfig.class);
    }

    public Client getClient(Long clientId, Long entityId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("entityId", entityId);
        return httpClient.buildRequest(HttpMethod.GET, CLIENTS_API_PATH + "/clients/{clientId}")
                .pathParams(clientId)
                .params(builder.build())
                .execute(Client.class);
    }

    public Customer getCustomer(String customerId) {
        return httpClient.buildRequest(HttpMethod.GET, CLIENTS_API_PATH + CUSTOMER_ID)
                .pathParams(customerId)
                .execute(Customer.class);
    }

    public CustomersResponse searchCustomers(SearchCustomersRequest request) {
        var params = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, CLIENTS_API_PATH + "/customers")
                .params(params)
                .execute(CustomersResponse.class);
    }

    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {

        return httpClient.buildRequest(HttpMethod.POST, CLIENTS_API_PATH + "/customers")
                .params(new QueryParameters.Builder().addQueryParameter("entityId", request.getEntityId()).build())
                .body(new ClientRequestBody(request))
                .execute(CreateCustomerResponse.class);

    }

    public void updateCustomer(String customerId, Integer entityId, Customer request) {
        httpClient.buildRequest(HttpMethod.PUT, CLIENTS_API_PATH + CUSTOMER_ID)
                .pathParams(customerId)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateCustomerAuthOrigins(String customerId, Integer entityId, List<AuthOrigin> request) {
        httpClient.buildRequest(HttpMethod.PUT, CLIENTS_API_PATH + CUSTOMER_ID + "/auth-origins")
                .pathParams(customerId)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateCustomerUser(String customerId, Integer entityId, Customer request) {
        httpClient.buildRequest(HttpMethod.PUT, CLIENTS_API_PATH + "/entities/{entityId}/users/{customerId}")
                .pathParams(entityId, customerId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public CustomerTypeProcessorResponse executeCustomertTypeAutomaticAssignment(String customerId, CustomerTypeAutomaticAssignment body) {
        return httpClient.buildRequest(HttpMethod.POST, CLIENTS_API_PATH + CUSTOMER_ID + "/customer-type-assignment")
                .pathParams(customerId)
                .body(new ClientRequestBody(body))
                .execute(CustomerTypeProcessorResponse.class);
    }

    public void createCustomers(String vendorId, CreateExternalCustomersRequest customersRequest) {
        httpClient.buildRequest(HttpMethod.POST, VENDORS_BASE_PATH + "/auth-vendors/{id}/create-users")
                .pathParams(vendorId)
                .body(new ClientRequestBody(customersRequest))
                .execute();
    }

    public void synchronizeExternalCustomers(String vendorId, String customerId) {
        httpClient.buildRequest(HttpMethod.POST, VENDORS_BASE_PATH + "/auth-vendors/{id}/synchronize-user/{userId}")
                .pathParams(vendorId, customerId)
                .execute();
    }
}
