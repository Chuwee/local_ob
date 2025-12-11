package es.onebox.mgmt.datasources.ms.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingExportFilter;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFilterTypeDTO;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorEntityConfig;
import es.onebox.mgmt.datasources.ms.client.dto.ClientsConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.CustomerSearchFilter;
import es.onebox.mgmt.datasources.ms.client.dto.CustomersSearch;
import es.onebox.mgmt.datasources.ms.client.dto.DeleteConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishing;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingFilterResponse;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsResponse;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Client;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientEntity;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientSecret;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUser;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUsers;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SearchClientsFilter;
import es.onebox.mgmt.exception.ApiMgmtCustomersErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtExportsErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.users.dto.UserSecretDTO;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsClientDatasource {

    private static final int TIMEOUT = 60000;

    private static final String VENDOR_API_VERSION = "v1";
    private static final String VENDOR_BASE_PATH = "/vendors-api/" + VENDOR_API_VERSION;
    private static final String CLIENTS_API_VERSION = "1.0";
    private static final String CLIENTS_API_PATH = "/clients-api/" + CLIENTS_API_VERSION;

    private static final String CLIENTS = CLIENTS_API_PATH + "/clients";
    private static final String USERS = CLIENTS_API_PATH + "/users";
    private static final String CUSTOMERS = CLIENTS_API_PATH + "/customers";
    private static final String CONDITIONS = CLIENTS_API_PATH + "/conditions";
    private static final String CONDITIONS_GROUP = CONDITIONS + "/{groupId}";
    private static final String CLIENTS_CONDITIONS = CLIENTS_API_PATH + "/conditions/clients";
    private static final String CLIENTS_ENTITIES = CLIENTS_API_PATH + "/clientEntities";
    private static final String CLIENT = CLIENTS + "/{clientId}";
    private static final String CLIENT_USERS = CLIENT + "/users";
    private static final String CLIENT_USER = "/{clientUserId}";
    private static final String APIKEY = CLIENTS_API_PATH + "/users" + CLIENT_USER + "/api-key";
    private static final String B2B_PUBLISHING = CLIENTS_API_PATH + "/b2b-publishing";
    private static final String B2B_PUBLISHING_EXPORT = B2B_PUBLISHING + "/seats/exports";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("400G0001", ApiMgmtCustomersErrorCode.CUSTOMER_PARAMETERS_ARE_WRONG);
        ERROR_CODES.put("400G0002", ApiMgmtCustomersErrorCode.CUSTOMER_BAD_PARAMETER_FORMAT);
        ERROR_CODES.put("409G0001", ApiMgmtCustomersErrorCode.CUSTOMER_ALREADY_EXISTS);
        ERROR_CODES.put("404CL0023", ApiMgmtCustomersErrorCode.CUSTOMER_NOT_FOUND);
        ERROR_CODES.put("400CL0028", ApiMgmtCustomersErrorCode.CUSTOMER_CUSTOMERS_NOT_ALLOW_FOR_ENTITY);
        ERROR_CODES.put("400CL0029", ApiMgmtCustomersErrorCode.CUSTOMER_DUPLICATED_MEMBER_ID);
        ERROR_CODES.put("404CL0030", ApiMgmtCustomersErrorCode.CUSTOMER_NOTE_NOT_FOUND);
        ERROR_CODES.put("404CL0031", ApiMgmtCustomersErrorCode.CUSTOMER_DELETE_NOT_ALLOWED_FOR_ENTITY);
        ERROR_CODES.put("409CL0002", ApiMgmtCustomersErrorCode.CUSTOMER_TARGET_ALREADY_MANAGED);
        ERROR_CODES.put("409CL0003", ApiMgmtCustomersErrorCode.CUSTOMER_DUPLICATED_MANAGEMENT);
        ERROR_CODES.put("409CL0004", ApiMgmtCustomersErrorCode.CUSTOMER_INVALID_TYPE);
        ERROR_CODES.put("409CL0005", ApiMgmtCustomersErrorCode.CUSTOMER_TARGET_INVALID_TYPE);
        ERROR_CODES.put("409CL0006", ApiMgmtCustomersErrorCode.CUSTOMER_TARGET_IS_MANAGER);
        ERROR_CODES.put("409CL0007", ApiMgmtCustomersErrorCode.CUSTOMER_IS_MANAGED);
        ERROR_CODES.put("409CL0008", ApiMgmtCustomersErrorCode.CUSTOMER_TARGET_NOT_MANAGED);
        ERROR_CODES.put("404CL0014", ApiMgmtCustomersErrorCode.CLIENT_NOT_FOUND);
        ERROR_CODES.put("404CL0005", ApiMgmtCustomersErrorCode.CLIENT_NOT_FOUND);

        ERROR_CODES.put("404CL0032", ApiMgmtCustomersErrorCode.CUSTOMER_DELETE_NOT_ALLOWED_ACTIVE_SEASON_TICKET_MEMBER_REQUIRED);
        ERROR_CODES.put("400CL0032", ApiMgmtCustomersErrorCode.CUSTOMER_ALREADY_LOCKED);
        ERROR_CODES.put("400CL0033", ApiMgmtCustomersErrorCode.CUSTOMER_ALREADY_ACTIVE);
        ERROR_CODES.put("400CL0035", ApiMgmtErrorCode.MEMBERS_ENTITY_NOT_ALLOWED);
        ERROR_CODES.put("412CL0036", ApiMgmtCustomersErrorCode.PENDING_CUSTOMER_IMPORT);
        ERROR_CODES.put("404CL0006", ApiMgmtCustomersErrorCode.CONDITIONS_NOT_FOUND);
        ERROR_CODES.put("400CL0013", ApiMgmtCustomersErrorCode.CONDITIONS_NOT_ALL_TYPES);
        ERROR_CODES.put("400CL0017", ApiMgmtCustomersErrorCode.CLIENT_TYPE_INVALID);
        ERROR_CODES.put("400CL0026", ApiMgmtCustomersErrorCode.CLIENT_USER_EXISTS);
        ERROR_CODES.put("400CL0027", ApiMgmtErrorCode.CLIENT_TAX_ID_ALREADY_IN_USE);
        ERROR_CODES.put("400CL0019", ApiMgmtCustomersErrorCode.BAD_UPDATE_REQUEST);
        ERROR_CODES.put("500C0002", ApiMgmtErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("EXPORT_LIMIT_REACHED", ApiMgmtExportsErrorCode.EXPORT_LIMIT_REACHED);
        ERROR_CODES.put("EXPORT_STATUS_NOT_FOUND", ApiMgmtExportsErrorCode.EXPORT_NOT_FOUND);
        ERROR_CODES.put("B2B_PUBLISHING_SEAT_NOT_FOUND", ApiMgmtErrorCode.B2B_PUBLISHING_SEAT_NOT_FOUND);
        ERROR_CODES.put("400CL0002", ApiMgmtErrorCode.SUBDIVISION_IN_COUNTRY_NOT_FOUND);
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
                .readTimeout(TIMEOUT)
                .build();
    }

    public AuthVendorEntityConfig getAuthVendorEntityConfiguration(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, VENDOR_BASE_PATH + "/entities/{entityId}")
                .pathParams(entityId)
                .execute(AuthVendorEntityConfig.class);
    }

    public void putAuthVendorEntityConfiguration(Long entityId, AuthVendorEntityConfig authVendorEntityConfig) {
        httpClient.buildRequest(HttpMethod.PUT, VENDOR_BASE_PATH + "/entities/{entityId}")
                .pathParams(entityId)
                .body(new ClientRequestBody(authVendorEntityConfig))
                .execute();
    }

    public List<AuthVendorConfig> getAuthVendors() {
        return httpClient.buildRequest(HttpMethod.GET, VENDOR_BASE_PATH + "/auth-vendors")
                .execute(ListType.of(AuthVendorConfig.class));
    }

    public AuthVendorConfig getAuthVendors(String authVendor) {
        return httpClient.buildRequest(HttpMethod.GET, VENDOR_BASE_PATH + "/auth-vendors/{authVendor}")
                .pathParams(authVendor)
                .execute(AuthVendorConfig.class);
    }

    public Clients getClients(SearchClientsFilter searchClientsFilter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(searchClientsFilter);
        return httpClient.buildRequest(HttpMethod.GET, CLIENTS)
                .params(builder.build())
                .execute(Clients.class);
    }

    public Client getClient(Long clientId, Long entityId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("entityId", entityId);
        return httpClient.buildRequest(HttpMethod.GET, CLIENT)
                .pathParams(clientId)
                .params(builder.build())
                .execute(Client.class);
    }

    public Client upsertClient(Client client, Long entityId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("entityId", entityId);
        return httpClient.buildRequest(HttpMethod.POST, CLIENTS)
                .params(builder.build())
                .body(new ClientRequestBody(client))
                .execute(Client.class);
    }

    public void deleteClient(Long clientId, Long entityId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("clientId", clientId);
        builder.addQueryParameter("entityId", entityId);
        httpClient.buildRequest(HttpMethod.DELETE, CLIENTS_ENTITIES)
                .params(builder.build())
                .execute();
    }

    public ConditionsData getConditions(ConditionsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, CONDITIONS)
                .params(builder.build())
                .execute(ConditionsData.class);
    }

    public ClientsConditionsData getClientConditions(ConditionsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, CLIENTS_CONDITIONS)
                .params(builder.build())
                .execute(ClientsConditionsData.class);
    }

    public void createConditions(List<ConditionsData> conditions) {
        httpClient.buildRequest(HttpMethod.POST, CONDITIONS)
                .body(new ClientRequestBody(conditions))
                .execute();
    }

    public void deleteConditions(Long groupId) {
        httpClient.buildRequest(HttpMethod.DELETE, CONDITIONS_GROUP)
                .pathParams(groupId)
                .execute();
    }

    public void deleteClientsConditions(DeleteConditionsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        httpClient.buildRequest(HttpMethod.DELETE, CLIENTS_CONDITIONS)
                .params(builder.build())
                .execute();
    }

    public ClientUser getClientUser(Long clientUserId) {
        return httpClient.buildRequest(HttpMethod.GET, USERS + CLIENT_USER)
                .pathParams(clientUserId)
                .execute(ClientUser.class);
    }

    public ClientUsers getClientUsers(Long clientId,
                                      String keyword,
                                      Integer from,
                                      Integer amount,
                                      Boolean countAllElements) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("keyword", keyword);
        builder.addQueryParameter("from", from);
        builder.addQueryParameter("amount", amount);
        builder.addQueryParameter("countAllElements", countAllElements);

        return httpClient.buildRequest(HttpMethod.GET, CLIENT_USERS)
                .pathParams(clientId)
                .params(builder.build())
                .execute(ClientUsers.class);
    }

    public ClientUser upsertClientUser(ClientUser clientUser, Long entityId, Boolean resetPassword) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("entityId", entityId);
        builder.addQueryParameter("resetPassword", resetPassword);
        return httpClient.buildRequest(HttpMethod.POST, USERS)
                .params(builder.build())
                .body(new ClientRequestBody(clientUser))
                .execute(ClientUser.class);
    }

    public ClientUser deleteClientUser(Long clientUserId) {
        return httpClient.buildRequest(HttpMethod.DELETE, USERS + CLIENT_USER)
                .pathParams(clientUserId)
                .execute(ClientUser.class);
    }

    public List<ClientUser> getClientUsersByUsername(String username) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("username", username);

        return httpClient.buildRequest(HttpMethod.GET, USERS + "/getClientsByUsername")
                .params(builder.build())
                .execute(ListType.of(ClientUser.class));
    }

    public void createClientEntity(List<ClientEntity> clientEntities) {
        httpClient.buildRequest(HttpMethod.POST, CLIENTS_ENTITIES)
                    .body(new ClientRequestBody(clientEntities))
                    .execute();
    }

    public SeatPublishingsResponse searchB2bSeatPublishings(SeatPublishingsFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, B2B_PUBLISHING + "/seats")
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(SeatPublishingsResponse.class);
    }

    public SeatPublishing getB2bSeatPublishing(Long id, List<Long> entityIds, Long operatorId) {
        return httpClient.buildRequest(HttpMethod.GET, B2B_PUBLISHING + "/seats/{id}")
                .pathParams(id)
                .params(new QueryParameters.Builder().addQueryParameter("entityIds", entityIds)
                        .addQueryParameter("operatorId", operatorId).build())
                .execute(SeatPublishing.class);
    }

    public SeatPublishingFilterResponse getSeatsFilterOptions(SeatPublishingFilterTypeDTO filterName, SeatPublishingsFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, B2B_PUBLISHING + "/seats/filters/{filterName}")
                .pathParams(filterName)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(SeatPublishingFilterResponse.class);
    }

    public ExportProcess exportProcess(SeatPublishingExportFilter exportFilter) {
        return httpClient.buildRequest(HttpMethod.POST, B2B_PUBLISHING_EXPORT)
                .body(new ClientRequestBody(exportFilter))
                .execute(ExportProcess.class);
    }

    public ExportProcess getSeatPublishingsReportStatus(String exportId, Long userId) {
        return httpClient.buildRequest(HttpMethod.POST, B2B_PUBLISHING_EXPORT)
                .pathParams(exportId, userId)
                .execute(ExportProcess.class);
    }

    public CustomersSearch findCustomers(Long entityId, CustomerSearchFilter customerSearchFilter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("entityId", entityId);
        if (!CommonUtils.isEmpty(customerSearchFilter.getCustomerIds())) {
            for (String customerId : customerSearchFilter.getCustomerIds()) {
                params.addQueryParameter("customerIds", customerId);
            }
        }

        return httpClient.buildRequest(HttpMethod.GET, CUSTOMERS)
                .params(params.build())
                .execute(CustomersSearch.class);
    }

    public ClientSecret refreshApiKey(Long userId) {
            return httpClient.buildRequest(HttpMethod.PUT, APIKEY)
                    .pathParams(userId)
                    .execute(ClientSecret.class);
    }
}
