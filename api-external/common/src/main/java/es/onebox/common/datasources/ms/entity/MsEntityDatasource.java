package es.onebox.common.datasources.ms.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.common.converters.ConvertUtils;
import es.onebox.common.datasources.common.dto.Category;
import es.onebox.common.datasources.ms.entity.dto.AuthConfigDTO;
import es.onebox.common.datasources.ms.entity.dto.CountryDTO;
import es.onebox.common.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.common.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.common.datasources.ms.entity.dto.Entities;
import es.onebox.common.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.common.datasources.ms.entity.dto.EntityConfig;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.common.datasources.ms.entity.dto.ExternalMgmtConfigDTO;
import es.onebox.common.datasources.ms.entity.dto.Language;
import es.onebox.common.datasources.ms.entity.dto.Operator;
import es.onebox.common.datasources.ms.entity.dto.Producer;
import es.onebox.common.datasources.ms.entity.dto.RequestEntityDTO;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.dto.UserSearchFilter;
import es.onebox.common.datasources.ms.entity.dto.Users;
import es.onebox.common.datasources.ms.entity.enums.EntityField;
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

@Component("msEntityDataSource")
public class MsEntityDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-entity-api/" + API_VERSION;
    private static final String OPERATORS = "/operators";
    private static final String OPERATOR = OPERATORS + "/{operatorId}";
    private static final String ENTITIES = "/entities";
    private static final String ENTITIES_CONFIG = ENTITIES + "/config";

    private static final String ENTITY = ENTITIES + "/{entityId}";

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("USER_NOT_FOUND", null);
        ERROR_CODES.put("ENTITY_NOT_FOUND", null);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsEntityDatasource(@Value("${clients.services.ms-entity}") String baseUrl,
                              TracingInterceptor tracingInterceptor,
                              ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public EntityDTO getEntity(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY)
                .pathParams(id)
                .execute(EntityDTO.class);
    }

    public void updateEntity(Long entityId, RequestEntityDTO entity) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY)
                .pathParams(entityId)
                .body(new ClientRequestBody(entity))
                .execute();
    }


    public User getUser(String username, String apiKey, Long operatorId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("username", username)
                .addQueryParameter("apiKey", apiKey)
                .addQueryParameter("operatorId", operatorId.toString())
                .build();

        return httpClient.buildRequest(HttpMethod.GET, "/users/auth")
                .params(params)
                .execute(User.class);
    }

    public Users getUsers(UserSearchFilter searchFilter) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(searchFilter)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, "/users")
                .params(params)
                .execute(Users.class);
    }

    public User getUser(Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, "/users/{userId}")
                .pathParams(userId)
                .execute(User.class);
    }

    public void updateUser(Long userId, User userUpdate) {
        httpClient.buildRequest(HttpMethod.PUT, "/users/{id}")
                .pathParams(userId)
                .body(new ClientRequestBody(userUpdate))
                .execute();
    }

    public  List<ExternalMgmtConfigDTO> getExternalMgmtConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}/external-management")
                .pathParams(entityId)
                .execute(ListType.of(ExternalMgmtConfigDTO.class));
    }

    public Producer getProducer(Long producerId) {
        return httpClient.buildRequest(HttpMethod.GET,"/producers/{producerId}")
                .pathParams(producerId)
                .execute(Producer.class);
    }

    public Language getLanguage(Long languageId) {
        return httpClient.buildRequest(HttpMethod.GET,"/masterdata/languages/{languageId}")
                .pathParams(languageId)
                .execute(Language.class);
    }

    public List<Language> findLanguages(String langCode) {
        QueryParameters params = new QueryParameters.Builder().addQueryParameter("code", langCode).build();
        return httpClient.buildRequest(HttpMethod.GET,"/masterdata/languages")
                .params(params)
                .execute(ListType.of(Language.class));
    }

    public List<CountryDTO> getCountries() {
        return httpClient.buildRequest(HttpMethod.GET, "/masterdata/countries")
                .execute(ListType.of(CountryDTO.class));
    }


    public List<CountrySubdivisionDTO> getCountrySubdivisions() {
        return httpClient.buildRequest(HttpMethod.GET, "/masterdata/countries/subdivision")
                .execute(ListType.of(CountrySubdivisionDTO.class));
    }

    public List<Category> getPersonalizedCategories(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}/categories").pathParams(entityId)
                .execute(ListType.of(Category.class));
    }

    public Operator getOperator(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, OPERATOR)
                .pathParams(entityId)
                .execute(Operator.class);
    }

    public CustomerTypes getCustomerTypes(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}/customer-types")
                .pathParams(entityId)
                .execute(CustomerTypes.class);
    }

    public Entities getEntities(EntitySearchFilter entitySearchFilter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(entitySearchFilter);
        ConvertUtils.checkSortFields(entitySearchFilter.getSort(), params, EntityField::byName);
        ConvertUtils.checkFilterFields(entitySearchFilter.getFields(), params, EntityField::byName);
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES)
                .params(params.build())
                .execute(Entities.class);
    }

    public List<EntityConfig> getEntitiesConfigs(List<Long> entities) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("entities", entities)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES_CONFIG)
                .params(params)
                .execute(ListType.of(EntityConfig.class));
    }

    public EntityBankAccount getEntityBankAccount(Long entityId, Long bankAccountId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY + "/bank-accounts/{bankAccountId}")
                .pathParams(entityId, bankAccountId)
                .execute(EntityBankAccount.class);
    }

    public AuthConfigDTO getAuthConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY + "/auth-config")
                .pathParams(entityId)
                .execute(AuthConfigDTO.class);
    }
}
