package es.onebox.mgmt.datasources.ms.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import es.onebox.mgmt.datasources.common.enums.SurchargeType;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.whatsapptemplates.WhatsappTemplates;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.contents.EntityLiterals;
import es.onebox.mgmt.datasources.ms.entity.dto.Attribute;
import es.onebox.mgmt.datasources.ms.entity.dto.Calendar;
import es.onebox.mgmt.datasources.ms.entity.dto.Category;
import es.onebox.mgmt.datasources.ms.entity.dto.CategoryMapping;
import es.onebox.mgmt.datasources.ms.entity.dto.CookieSettings;
import es.onebox.mgmt.datasources.ms.entity.dto.CookieSettingsBase;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOneboxInvoiceEntityRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorTaxRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorsResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.DonationProviders;
import es.onebox.mgmt.datasources.ms.entity.dto.Entities;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityCustomContents;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityFriendsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityGatewayConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityInvoiceConfigurationSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityProfile;
import es.onebox.mgmt.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlock;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlockFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityVisibilities;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ForgotPasswordPropertiesResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.ForgotPswRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.ForgotPwdResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.Form;
import es.onebox.mgmt.datasources.ms.entity.dto.GenerateOneboxInvoiceRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoiceProviderInfo;
import es.onebox.mgmt.datasources.ms.entity.dto.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.MFARequest;
import es.onebox.mgmt.datasources.ms.entity.dto.MFAResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.CountryWithTaxCalculationDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataCountryValue;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.Notification;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntities;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntitiesFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType;
import es.onebox.mgmt.datasources.ms.entity.dto.Operators;
import es.onebox.mgmt.datasources.ms.entity.dto.OperatorsSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInoivcePrefixFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Producers;
import es.onebox.mgmt.datasources.ms.entity.dto.RecoverForgotPasswordRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.RequestInvoiceProvider;
import es.onebox.mgmt.datasources.ms.entity.dto.Role;
import es.onebox.mgmt.datasources.ms.entity.dto.Roles;
import es.onebox.mgmt.datasources.ms.entity.dto.SecurityConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Tax;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplateZones;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesUpdateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateEntityCustomContents;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOneboxInvoiceEntityRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorTaxesRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateSecurityConfigRequestDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.dto.UserAuthUrls;
import es.onebox.mgmt.datasources.ms.entity.dto.UserFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.UserLimits;
import es.onebox.mgmt.datasources.ms.entity.dto.Users;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsImages;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsTexts;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.CustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.UpdateCustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.membercounter.MemberCounter;
import es.onebox.mgmt.datasources.ms.entity.dto.membercounter.UpdateMemberCounter;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeCreateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeUpdateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit.UserRateLimitConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.ResourceServer;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.UserRealmConfigCreateDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.UserRealmConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.TaxType;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalCreateRequest;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalResponse;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalSearchResponse;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalUpdateRequest;
import es.onebox.mgmt.datasources.ms.insurance.dto.ResponseEntities;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocks;
import es.onebox.mgmt.entities.dto.AttributeSearchFilter;
import es.onebox.mgmt.entities.enums.EntityField;
import es.onebox.mgmt.entities.enums.EntityImageContentType;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.operators.dto.OperatorCurrencies;
import es.onebox.mgmt.operators.dto.UpdateOperatorCurrencyRequest;
import es.onebox.mgmt.users.dto.UpdateVisibilityDTO;
import es.onebox.mgmt.users.dto.UserSecretDTO;
import es.onebox.mgmt.users.enums.UserField;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MsEntityDatasource extends MsEntityDefinition {

    private final HttpClient httpClient;

    @Autowired
    public MsEntityDatasource(@Value("${clients.services.ms-entity}") String baseUrl,
                              ObjectMapper jacksonMapper,
                              TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public User getUser(Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, USER)
                .pathParams(userId)
                .execute(User.class);
    }

    public User getAuthUserByUserName(String username, Long entityId) {
        return getAuthUser(username, entityId, null);
    }

    public User getAuthUserByApiKey(String apiKey, Long entityId) {
        return getAuthUser(null, entityId, apiKey);
    }

    public User getAuthUser(String username, Long entityId, String apiKey) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("username", username)
                .addQueryParameter("entityId", entityId.toString())
                .addQueryParameter("apiKey", apiKey)
                .build();

        return httpClient.buildRequest(HttpMethod.GET, USERS + "/auth")
                .params(params)
                .execute(User.class);
    }

    public Users getUser(String username, Long operatorId, String apiKey) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("username", username)
                .addQueryParameter("apiKey", apiKey)
                .addQueryParameter("operatorId", operatorId.toString())
                .build();

        return httpClient.buildRequest(HttpMethod.GET, USERS)
                .params(params)
                .execute(Users.class);
    }

    public Users getUsers(UserFilter request) {
        QueryParameters.Builder params = new QueryParameters.Builder()
                .addQueryParameters(request);
        ConverterUtils.checkSortFields(request.getSort(), params, UserField::byName);

        return httpClient.buildRequest(HttpMethod.GET, USERS)
                .params(params.build())
                .execute(Users.class);
    }

    public UserSecretDTO createUser(User user) {
        return httpClient.buildRequest(HttpMethod.POST, USERS)
                .body(new ClientRequestBody(user))
                .execute(UserSecretDTO.class);
    }

    public void updateUser(User user) {
        httpClient.buildRequest(HttpMethod.PUT, USER)
                .pathParams(user.getId())
                .body(new ClientRequestBody(user))
                .execute();
    }

    public void deleteUser(Long userId) {
        httpClient.buildRequest(HttpMethod.DELETE, USER)
                .pathParams(userId)
                .execute();
    }

    public List<Role> getUserRoles(Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, USER + ROLES)
                .pathParams(userId)
                .execute(ListType.of(Role.class));
    }

    public void setRole(Long userId, Role role) {
        httpClient.buildRequest(HttpMethod.PUT, USER + ROLES)
                .pathParams(userId)
                .body(new ClientRequestBody(role))
                .execute();
    }

    public void setRoles(Long userId, Roles roles) {
        httpClient.buildRequest(HttpMethod.POST, USER + ROLES)
                .pathParams(userId)
                .body(new ClientRequestBody(roles))
                .execute();
    }

    public void unsetRole(Long userId, String roleCode) {
        httpClient.buildRequest(HttpMethod.DELETE, USER + ROLE)
                .pathParams(userId, roleCode)
                .execute();
    }

    public void addPermission(Long userId, String roleCode, String permissionCode) {
        httpClient.buildRequest(HttpMethod.POST, USER + ROLE + PERMISSION)
                .pathParams(userId, roleCode, permissionCode)
                .execute();
    }

    public void deletePermission(Long userId, String roleCode, String permissionCode) {
        httpClient.buildRequest(HttpMethod.DELETE, USER + ROLE + PERMISSION)
                .pathParams(userId, roleCode, permissionCode)
                .execute();
    }

    public List<Role> getAllRoles() {
        return httpClient.buildRequest(HttpMethod.GET, ROLES)
                .execute(ListType.of(Role.class));
    }

    public ForgotPwdResponse forgotPassword(String email) {
        ForgotPswRequest request = new ForgotPswRequest(email);
        return httpClient.buildRequest(HttpMethod.POST, USERS + "/forgot-password")
                .body(new ClientRequestBody(request))
                .execute(ForgotPwdResponse.class);
    }

    public ForgotPasswordPropertiesResponse validateToken(String token) {
        QueryParameters.Builder queryParams = new QueryParameters.Builder().addQueryParameter("token", token);
        return httpClient.buildRequest(HttpMethod.GET, USERS + "/forgot-password")
                .params(queryParams.build())
                .execute(ForgotPasswordPropertiesResponse.class);
    }

    public void recoverForgotPassword(RecoverForgotPasswordRequest request) {
        httpClient.buildRequest(HttpMethod.POST, USERS + "/forgot-password/recover")
                .body(new ClientRequestBody(request))
                .execute(Long.class);
    }

    public Entity getEntity(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY)
                .pathParams(entityId)
                .execute(Entity.class);
    }

    public List<Tax> getEntityTaxes(Long entityId, Long eventId, Long venueId, TaxType taxType) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("eventId", eventId)
                .addQueryParameter("venueId", venueId)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, ENTITY + TAX_IDS)
                .pathParams(entityId, taxType)
                .params(params)
                .execute(ListType.of(Tax.class));
    }

    public es.onebox.mgmt.datasources.ms.entity.dto.Operator getOperator(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, OPERATOR)
                .pathParams(entityId)
                .execute(es.onebox.mgmt.datasources.ms.entity.dto.Operator.class);
    }

    public OperatorCurrencies getOperatorCurrency(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, OPERATOR_CURRENCY)
                .pathParams(entityId)
                .execute(OperatorCurrencies.class);
    }

    public void addOperatorCurrencies(Long entityId, UpdateOperatorCurrencyRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, OPERATOR_CURRENCY)
                .pathParams(entityId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public Operators searchOperators(OperatorsSearchFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, OPERATORS)
                .params(params.build())
                .execute(Operators.class);
    }

    public void updateOperator(Long operatorId, UpdateOperatorRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, OPERATOR)
                .pathParams(operatorId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public CreateOperatorsResponse createOperator(CreateOperatorRequest request) {
        return httpClient.buildRequest(HttpMethod.POST, OPERATORS)
                .body(new ClientRequestBody(request))
                .execute(CreateOperatorsResponse.class);
    }

    public Entities getEntities(EntitySearchFilter entitySearchFilter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(entitySearchFilter);
        ConverterUtils.checkSortFields(entitySearchFilter.getSort(), params, EntityField::byName);
        ConverterUtils.checkFilterFields(entitySearchFilter.getFields(), params, EntityField::byName);
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES)
                .params(params.build())
                .execute(Entities.class);
    }

    public Long createEntity(Entity entity) {
        return httpClient.buildRequest(HttpMethod.POST, ENTITIES)
                .body(new ClientRequestBody(entity))
                .execute(Long.class);
    }

    public void updateEntity(Entity entity) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY)
                .pathParams(entity.getId())
                .body(new ClientRequestBody(entity))
                .execute();
    }

    public List<EntityTypes> getEntityTypes(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/types")
                .pathParams(entityId)
                .execute(ListType.of(EntityTypes.class));
    }

    public void setEntityType(Long entityId, EntityTypes entityType) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}/types/{entityType}")
                .pathParams(entityId, entityType.name())
                .execute();
    }

    public void unsetEntityType(Long entityId, EntityTypes entityType) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITIES + "/{entityId}/types/{entityType}")
                .pathParams(entityId, entityType.name())
                .execute();
    }

    public Producer getProducer(Long producerId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCERS + "/{producerId}")
                .pathParams(producerId)
                .execute(Producer.class);
    }

    public Producers getProducers(ProducerFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);

        return httpClient.buildRequest(HttpMethod.GET, PRODUCERS)
                .params(params.build())
                .execute(Producers.class);
    }

    public Producers getProducersByEntityId(Long entityId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("entityId", entityId);

        return httpClient.buildRequest(HttpMethod.GET, PRODUCERS)
                .params(params.build())
                .execute(Producers.class);
    }

    public Long createProducer(Producer producer) {
        return httpClient.buildRequest(HttpMethod.POST, PRODUCERS)
                .body(new ClientRequestBody(producer))
                .execute(Long.class);
    }

    public void updateProducer(Producer producer) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCERS + "/{producerId}")
                .pathParams(producer.getId())
                .body(new ClientRequestBody(producer))
                .execute();
    }

    public List<EntityTax> getTaxes(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/taxes")
                .pathParams(entityId)
                .execute(ListType.of(EntityTax.class));
    }

    public List<EntityTax> getOperatorTaxes(Long operatorId) {
        return httpClient.buildRequest(HttpMethod.GET, OPERATOR + "/taxes")
                .pathParams(operatorId)
                .execute(ListType.of(EntityTax.class));
    }

    public IdDTO createOperatorTax(Long operatorId, CreateOperatorTaxRequest createOperatorTaxRequest) {
        return httpClient.buildRequest(HttpMethod.POST, OPERATOR + "/taxes")
                .pathParams(operatorId)
                .body(new ClientRequestBody(createOperatorTaxRequest))
                .execute(IdDTO.class);
    }

    public void updateOperatorTaxes(Long operatorId, UpdateOperatorTaxesRequest updateOperatorTaxesRequest) {
        httpClient.buildRequest(HttpMethod.PUT, OPERATOR + "/taxes")
                .pathParams(operatorId)
                .body(new ClientRequestBody(updateOperatorTaxesRequest))
                .execute();
    }

    public Calendar getCalendar(Long entityId, long calendarId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/calendars/{calendarId}")
                .pathParams(entityId, calendarId)
                .execute(Calendar.class);
    }

    public List<Calendar> getCalendars(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/calendars")
                .pathParams(entityId)
                .execute(ListType.of(Calendar.class));
    }

    public Long createCalendar(Long entityId, Calendar calendar) {
        return httpClient.buildRequest(HttpMethod.POST, ENTITIES + "/{entityId}/calendars")
                .pathParams(entityId)
                .body(new ClientRequestBody(calendar))
                .execute(Long.class);
    }

    public void updateCalendar(long entityId, Calendar calendar) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}/calendars/{calendarId}")
                .pathParams(entityId, calendar.getId())
                .body(new ClientRequestBody(calendar))
                .execute();
    }

    public void deleteCalendar(long entityId, Long calendarId) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITIES + "/{entityId}/calendars/{calendarId}")
                .pathParams(entityId, calendarId)
                .execute();
    }

    public Attribute getAttribute(long entityId, long attributeId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/attributes/{attributeId}")
                .pathParams(entityId, attributeId)
                .execute(Attribute.class);
    }

    public List<Attribute> getAttributes(Long entityId, AttributeSearchFilter attributeSearchFilter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (attributeSearchFilter != null && attributeSearchFilter.getScope() != null) {
            params.addQueryParameter("scope", attributeSearchFilter.getScope().getId());
        }
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/attributes")
                .pathParams(entityId)
                .params(params.build())
                .execute(ListType.of(Attribute.class));
    }

    public Long createAttribute(Long entityId, Attribute attribute) {
        return httpClient.buildRequest(HttpMethod.POST, ENTITIES + "/{entityId}/attributes")
                .pathParams(entityId)
                .body(new ClientRequestBody(attribute))
                .execute(Long.class);
    }

    public void updateAttribute(long entityId, Attribute attribute) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}/attributes/{attributeId}")
                .pathParams(entityId, attribute.getId())
                .body(new ClientRequestBody(attribute))
                .execute();
    }

    public void deleteAttribute(Long entityId, Long attributeId) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITIES + "/{entityId}/attributes/{calendarId}")
                .pathParams(entityId, attributeId)
                .execute();
    }

    public MasterdataValue getLanguage(Long languageId) {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/languages/{languageId}")
                .pathParams(languageId)
                .execute(MasterdataValue.class);
    }

    public List<MasterdataValue> getLanguages(String languageCode, Boolean platformLanguage) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (languageCode != null) {
            params.addQueryParameter("code", languageCode);
        }
        if (platformLanguage != null) {
            params.addQueryParameter("platformLanguage", BooleanUtils.toStringTrueFalse(platformLanguage));
        }

        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/languages")
                .params(params.build())
                .execute(ListType.of(MasterdataValue.class));
    }

    public MasterdataValue getCountry(Long countryId) {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + COUNTRY)
                .pathParams(countryId)
                .execute(MasterdataValue.class);
    }

    public List<MasterdataValue> getCountries(String countryCode, Boolean systemCountry) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        ConverterUtils.addQueryParameter(params, "code", countryCode);
        ConverterUtils.addQueryParameter(params, "systemCountry", systemCountry);

        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + COUNTRIES)
                .params(params.build())
                .execute(ListType.of(MasterdataValue.class));
    }

    public List<CountryWithTaxCalculationDTO> getCountriesWithTaxCalculation(String countryCode, Boolean systemCountry) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        ConverterUtils.addQueryParameter(params, "code", countryCode);
        ConverterUtils.addQueryParameter(params, "systemCountry", systemCountry);

        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + COUNTRIES)
                .params(params.build())
                .execute(ListType.of(CountryWithTaxCalculationDTO.class));
    }

    public List<MasterdataValue> getCountrySubdivisionByCountryId(Long countryId) {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + COUNTRY_SUBDIVISIONS)
                .pathParams(countryId)
                .execute(ListType.of(MasterdataValue.class));
    }

    public MasterdataValue getCountrySubdivision(Long countrySubdivisionId) {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/countries/subdivision/{countrySubdivisionId}")
                .pathParams(countrySubdivisionId)
                .execute(MasterdataValue.class);
    }

    public List<MasterdataValue> getCountrySubdivisions(String countrySubdivisionCode) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (countrySubdivisionCode != null) {
            params.addQueryParameter("code", countrySubdivisionCode);
        }

        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/countries/subdivision")
                .params(params.build())
                .execute(ListType.of(MasterdataValue.class));
    }

    public List<TimeZone> getTimeZones() {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/timezones")
                .execute(ListType.of(TimeZone.class));
    }

    public List<Surcharge> getSurcharges(Long entityId, List<SurchargeType> surchargeTypes, List<Long> currencyIds) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (surchargeTypes != null) {
            params.addQueryParameter("type", surchargeTypes);
        }
        if (currencyIds != null) {
            params.addQueryParameter("currencyId", currencyIds);
        }
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/surcharges")
                .pathParams(entityId)
                .params((params.build()))
                .execute(ListType.of(Surcharge.class));
    }

    public void setSurcharge(Long entityId, List<Surcharge> surcharges) {
        httpClient.buildRequest(HttpMethod.POST, ENTITIES + "/{entityId}/surcharges")
                .pathParams(entityId)
                .body(new ClientRequestBody(surcharges))
                .execute();
    }

    public ExternalBarcodeEntityConfig getExternalBarcodeEntityConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/externalBarcodeConfig")
                .pathParams(entityId)
                .execute(ExternalBarcodeEntityConfig.class);
    }

    public void putExternalBarcodeEntityConfig(Long entityId, ExternalBarcodeEntityConfig externalBarcodeEntityConfig) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}/externalBarcodeConfig")
                .pathParams(entityId)
                .body(new ClientRequestBody(externalBarcodeEntityConfig))
                .execute(ExternalBarcodeEntityConfig.class);
    }

    public List<ExternalBarcodeConfig> getExternalBarcodes() {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/externalBarcodeConfig")
                .execute(ListType.of(ExternalBarcodeConfig.class));
    }

    public ExternalBarcodeConfig getEntityExternalBarcodeConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/externalBarcodeConfigFile")
                .pathParams(entityId)
                .execute(ExternalBarcodeConfig.class);
    }

    public List<Category> getEntityCategories(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/categories")
                .pathParams(entityId)
                .execute(ListType.of(Category.class));
    }

    public Category getEntityCategory(Long entityId, Long categoryId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/categories/{categoryId}")
                .pathParams(entityId, categoryId)
                .execute(Category.class);
    }

    public List<Category> getBaseCategories() {
        return httpClient.buildRequest(HttpMethod.GET, "/categories")
                .execute(ListType.of(Category.class));
    }

    public Category getCategory(Integer categoryId) {
        return httpClient.buildRequest(HttpMethod.GET, "/categories/{categoryId}")
                .pathParams(categoryId)
                .execute(Category.class);
    }

    public void deleteEntityCategory(Long entityId, Long categoryId) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITIES + "/{entityId}/categories/{categoryId}")
                .pathParams(entityId, categoryId)
                .execute(Category.class);
    }

    public Long createEntityCategory(Long entityId, Category category) {
        return httpClient.buildRequest(HttpMethod.POST, ENTITIES + "/{entityId}/categories")
                .pathParams(entityId)
                .body(new ClientRequestBody(category))
                .execute(Long.class);
    }

    public void updateEntityCategory(Long entityId, Long categoryId, Category category) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}/categories/{categoryId}")
                .pathParams(entityId, categoryId)
                .body(new ClientRequestBody(category))
                .execute(Long.class);
    }

    public void putCategoryMapping(Long entityId, List<CategoryMapping> mappings) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITIES + "/{entityId}/categories/mapping")
                .pathParams(entityId)
                .body(new ClientRequestBody(mappings))
                .execute();
    }

    public List<CategoryMapping> getCategoryMapping(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/categories/mapping")
                .pathParams(entityId)
                .execute(ListType.of(CategoryMapping.class));
    }

    public List<Notification> getUserNotifications(Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, USER + "/notifications")
                .pathParams(userId)
                .execute(ListType.of(Notification.class));
    }

    public void setUserNotifications(Long userId, List<Notification> notifications) {
        httpClient.buildRequest(HttpMethod.PUT, USER + "/notifications")
                .pathParams(userId)
                .body(new ClientRequestBody(notifications))
                .execute(Long.class);
    }

    public List<Long> getVisibleEntities(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/visible/entities")
                .pathParams(entityId)
                .execute(ListType.of(Long.class));
    }


    public UserAuthUrls getUserAuthUrls(Long userId, boolean oldCpanel, Long impersonatedUserId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("oldCpanel", oldCpanel);
        params.addQueryParameter("impersonatedUserId", impersonatedUserId);
        return httpClient.buildRequest(HttpMethod.GET, MICROSTRATEGY + "/{userId}/auth")
                .pathParams(userId)
                .params(params.build())
                .execute(UserAuthUrls.class);
    }

    public Boolean userHasSubscriptions(Long userId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        return httpClient.buildRequest(HttpMethod.GET, MICROSTRATEGY + "/{userId}/subscriptions")
                .pathParams(userId)
                .params(params.build())
                .execute(Boolean.class);
    }

    public Boolean isSupersetUser(Long userId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        return httpClient.buildRequest(HttpMethod.GET, MICROSTRATEGY + "/{userId}/superset")
                .pathParams(userId)
                .params(params.build())
                .execute(Boolean.class);
    }

    public Boolean userCanImpersonate(Long userId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        return httpClient.buildRequest(HttpMethod.GET, MICROSTRATEGY + "/{userId}/impersonation")
                .pathParams(userId)
                .params(params.build())
                .execute(Boolean.class);
    }

    public List<EntityProfile> getEntityProfiles(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, PROFILES)
                .pathParams(entityId)
                .execute(ListType.of(EntityProfile.class));
    }

    public EntityProfile getEntityProfile(Long entityId, Long profileId) {
        return httpClient.buildRequest(HttpMethod.GET, PROFILE)
                .pathParams(entityId, profileId)
                .execute(EntityProfile.class);
    }

    public void deleteEntityProfile(Long entityId, Long profileId) {
        httpClient.buildRequest(HttpMethod.DELETE, PROFILE)
                .pathParams(entityId, profileId)
                .execute(EntityProfile.class);
    }

    public void updateEntityProfile(Long entityId, Long profileId, EntityProfile profile) {
        httpClient.buildRequest(HttpMethod.PUT, PROFILE)
                .pathParams(entityId, profileId)
                .body(new ClientRequestBody(profile))
                .execute(Long.class);
    }

    public IdDTO createEntityProfile(Long entityId, EntityProfile profile) {
        return httpClient.buildRequest(HttpMethod.POST, PROFILES)
                .pathParams(entityId)
                .body(new ClientRequestBody(profile))
                .execute(IdDTO.class);
    }

    public List<IdNameDTO> getDocumentTypesByOperatorId(Long operatorId) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("operatorId", operatorId);
        return httpClient.buildRequest(HttpMethod.GET, DOCUMENT_TYPES)
                .params(params.build())
                .execute(ListType.of(IdNameDTO.class));
    }

    public ProducerInvoicePrefix getProducerInvoicePrefixes(Long producerId, ProducerInoivcePrefixFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, INVOICE_PREFIXES)
                .pathParams(producerId)
                .params(params.build())
                .execute(ProducerInvoicePrefix.class);
    }

    public IdDTO createProducerInvoicePrefix(Long producerId, CreateProducerInvoicePrefix createProducerInvoicePrefix) {
        return httpClient.buildRequest(HttpMethod.POST, INVOICE_PREFIXES)
                .pathParams(producerId)
                .body(new ClientRequestBody(createProducerInvoicePrefix))
                .execute(IdDTO.class);
    }

    public void updateProducerInvoicePrefix(Long producerId, Long invoicePrefixId, UpdateProducerInvoicePrefix updateProducerInvoicePrefix) {
        httpClient.buildRequest(HttpMethod.PUT, INVOICE_PREFIX)
                .pathParams(producerId, invoicePrefixId)
                .body(new ClientRequestBody(updateProducerInvoicePrefix))
                .execute();
    }

    public InvoicePrefix getInvoicePrefix(Long producerId, Long invoicePrefixId) {
        return httpClient.buildRequest(HttpMethod.GET, INVOICE_PREFIX)
                .pathParams(producerId, invoicePrefixId)
                .execute(InvoicePrefix.class);
    }

    public InvoiceProviderInfo getInvoiceProvider(Long producerId) {
        return httpClient.buildRequest(HttpMethod.GET, INVOICE_PROVIDERS)
                .pathParams(producerId)
                .execute(InvoiceProviderInfo.class);
    }

    public InvoiceProviderInfo requestInvoiceProvider(Long producerId, RequestInvoiceProvider request) {
        return httpClient.buildRequest(HttpMethod.POST, INVOICE_PROVIDERS)
                .pathParams(producerId)
                .body(new ClientRequestBody(request))
                .execute(InvoiceProviderInfo.class);
    }

    public UserSecretDTO refreshApiKey(Long userId) {
        return httpClient.buildRequest(HttpMethod.PUT, APIKEY)
                .pathParams(userId)
                .execute(UserSecretDTO.class);
    }

    public List<Currency> getCurrencies() {
        return httpClient.buildRequest(HttpMethod.GET, CURRENCIES)
                .execute(ListType.of(Currency.class));
    }


    public void generateInvoice(GenerateOneboxInvoiceRequest request) {
        httpClient.buildRequest(HttpMethod.POST, ONEBOX_INVOICING)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public OneboxInvoiceEntitiesFilter getEntitiesFilter() {
        return httpClient.buildRequest(HttpMethod.GET, ONEBOX_INVOICING_ENTITIES_FILTER)
                .execute(OneboxInvoiceEntitiesFilter.class);
    }

    public OneboxInvoiceEntities getEntitiesInvoiceConfiguration(EntityInvoiceConfigurationSearchFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);

        return httpClient.buildRequest(HttpMethod.GET, ONEBOX_INVOICING_ENTITIES)
                .params(params.build())
                .execute(OneboxInvoiceEntities.class);
    }

    public void createEntityInvoiceConfiguration(Long entityId, CreateOneboxInvoiceEntityRequest request) {
        httpClient.buildRequest(HttpMethod.POST, ONEBOX_INVOICING_ENTITY)
                .pathParams(entityId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateEntityInvoiceConfiguration(Long entityId, OneboxInvoiceType type, UpdateOneboxInvoiceEntityRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, ONEBOX_INVOICING_ENTITY_TYPE)
                .pathParams(entityId, type)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public UserLimits getUserLimits() {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + USER_LIMITS)
                .execute(UserLimits.class);
    }

    public EntityVisibilities getEntityVisibilities(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY + VISIBILITIES)
                .pathParams(entityId)
                .execute(EntityVisibilities.class);
    }

    public void updateEntityVisibilities(Long entityId, EntityVisibilities entityVisibilities) {
        httpClient.buildRequest(HttpMethod.POST, ENTITY + VISIBILITIES)
                .pathParams(entityId)
                .body(new ClientRequestBody(entityVisibilities))
                .execute();
    }

    public List<EntityCustomContents> getCustomContents(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY + CUSTOM_CONTENTS)
                .pathParams(entityId)
                .execute(ListType.of(EntityCustomContents.class));
    }

    public void setCustomContents(Long entityId, List<UpdateEntityCustomContents> updateEntityCustomContents) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY + CUSTOM_CONTENTS)
                .pathParams(entityId)
                .body(new ClientRequestBody(updateEntityCustomContents))
                .execute();
    }

    public void deleteCustomContents(Long entityId, String tag) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITY + CUSTOM_CONTENTS + TAG)
                .pathParams(entityId, tag)
                .execute();
    }

    public CookieSettings getCookieSettings(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY + "/cookies")
                .pathParams(entityId)
                .execute(CookieSettings.class);
    }

    public void updateCookieSettings(Long entityId, CookieSettingsBase cookieSettings) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY + "/cookies")
                .pathParams(entityId)
                .body(new ClientRequestBody(cookieSettings))
                .execute();
    }

    public ExternalConfig getExternalConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_CONFIG)
                .pathParams(entityId)
                .execute(ExternalConfig.class);
    }

    public void updateExternalConfig(Long entityId, ExternalConfig externalConfig) {
        httpClient.buildRequest(HttpMethod.PUT, EXTERNAL_CONFIG)
                .pathParams(entityId)
                .body(new ClientRequestBody(externalConfig))
                .execute();
    }

    public List<MasterdataCountryValue> getAllInternationalPhonePrefixes() {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + COUNTRIES)
                .execute(ListType.of(MasterdataCountryValue.class));
    }

    public List<ResourceServer> getAllResourceServers() {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + RESOURCE_SERVERS)
                .execute(ListType.of(ResourceServer.class));
    }

    public List<ResourceServer> getAllAvailableResourceServers(Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, USER + AVAILABLE_RESOURCE_SERVERS)
                .pathParams(userId)
                .execute(ListType.of(ResourceServer.class));
    }

    public UserRealmConfigDTO getUserRealmConfig(Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, USER + "/realms-config")
                .pathParams(userId)
                .execute(UserRealmConfigDTO.class);
    }

    public void upsertUserRealmConfig(Long userId, UserRealmConfigCreateDTO create) {
        httpClient.buildRequest(HttpMethod.POST, USER + "/realms-config")
                .pathParams(userId)
                .body(new ClientRequestBody(create))
                .execute();
    }

    public IdDTO createTerminal(TerminalCreateRequest terminalCreateRequest) {
        return httpClient.buildRequest(HttpMethod.POST, TERMINALS)
                .body(new ClientRequestBody(terminalCreateRequest))
                .execute(IdDTO.class);
    }

    public TerminalSearchResponse searchTerminals(TerminalSearchFilter terminalSearchFilter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(terminalSearchFilter);
        return httpClient.buildRequest(HttpMethod.GET, TERMINALS)
                .params(params.build())
                .execute(TerminalSearchResponse.class);
    }

    public TerminalResponse getTerminal(Integer terminalId) {
        return httpClient.buildRequest(HttpMethod.GET, TERMINAL)
                .pathParams(terminalId)
                .execute(TerminalResponse.class);
    }

    public void updateTerminal(Integer terminalId, TerminalUpdateRequest terminalUpdateRequest) {
        httpClient.buildRequest(HttpMethod.PUT, TERMINAL)
                .pathParams(terminalId)
                .body(new ClientRequestBody(terminalUpdateRequest))
                .execute();
    }

    public void deleteTerminal(Integer terminalId) {
        httpClient.buildRequest(HttpMethod.DELETE, TERMINAL)
                .pathParams(terminalId)
                .execute();
    }

    public void regenerateTerminalLicense(Integer terminalId) {
        httpClient.buildRequest(HttpMethod.POST, TERMINAL + "/regenerate-license")
                .pathParams(terminalId)
                .execute();
    }

    public UserRateLimitConfig searchRateLimit(Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, USER + RATE_LIMIT)
                .pathParams(userId)
                .execute(UserRateLimitConfig.class);
    }

    public void upsertRateLimit(Long userId, UserRateLimitConfig userRateLimitConfig) {
        httpClient.buildRequest(HttpMethod.POST, USER + RATE_LIMIT)
                .pathParams(userId)
                .body(new ClientRequestBody(userRateLimitConfig))
                .execute();
    }

    public DonationProviders getDonationProviders() {
        return httpClient.buildRequest(HttpMethod.GET, DONATION_PROVIDERS)
                .execute(DonationProviders.class);
    }


    public WhatsappTemplates getWhatsappTemplatesContents(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, WHATSAPP_TEMPLATES)
                .pathParams(entityId)
                .execute(WhatsappTemplates.class);
    }

    public AuthConfig getAuthConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, AUTH_CONFIG)
                .pathParams(entityId)
                .execute(AuthConfig.class);
    }

    public void updateAuthConfig(Long entityId, AuthConfig authConfig) {
        httpClient.buildRequest(HttpMethod.PUT, AUTH_CONFIG)
                .pathParams(entityId)
                .body(new ClientRequestBody(authConfig))
                .execute();
    }


    public Form getForm(Long entityId, String formType) {
        return httpClient.buildRequest(HttpMethod.GET, FORMS + FORM_TYPE)
                .pathParams(entityId, formType)
                .execute(Form.class);
    }

    public void updateForm(Long entityId, Form updateForm, String formType) {
        httpClient.buildRequest(HttpMethod.PUT, FORMS + FORM_TYPE)
                .pathParams(entityId, formType)
                .body(new ClientRequestBody(updateForm))
                .execute(Form.class);
    }

    public MFAResponse sendMFAActivationEmail(Long userId, MFARequest request) {
        return httpClient.buildRequest(HttpMethod.POST, USER_MFA_ACTIVATION_SEND)
                .pathParams(userId)
                .body(new ClientRequestBody(request))
                .execute(MFAResponse.class);
    }

    public MFAResponse validateAndActivateMFA(Long userId, MFARequest request) {
        return httpClient.buildRequest(HttpMethod.POST, USER_MFA_ACTIVATION_CONFIRM)
                .pathParams(userId)
                .body(new ClientRequestBody(request))
                .execute(MFAResponse.class);
    }

    public LoyaltyPointsConfig getLoyaltyPoints(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, LOYALTY_PROGRAM + "/config")
                .pathParams(entityId)
                .execute(LoyaltyPointsConfig.class);
    }

    public void updateLoyaltyPoints(Long entityId, UpdateLoyaltyPointsConfig updateLoyaltyPointsConfig) {
        httpClient.buildRequest(HttpMethod.PUT, LOYALTY_PROGRAM + "/config")
                .pathParams(entityId)
                .body(new ClientRequestBody(updateLoyaltyPointsConfig))
                .execute(UpdateLoyaltyPointsConfig.class);
    }

    public SecurityConfigDTO getEntitySecurityConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_SECURITY_CONFIG)
                .pathParams(entityId)
                .execute(SecurityConfigDTO.class);
    }

    public void updateEntitySecurityConfig(Long entityId, UpdateSecurityConfigRequestDTO updateSecurityConfigRequestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_SECURITY_CONFIG)
                .pathParams(entityId)
                .body(new ClientRequestBody(updateSecurityConfigRequestDTO))
                .execute();
    }

    public List<IdNameDTO> getCustomerTriggers() {
        return httpClient.buildRequest(HttpMethod.GET, CUSTOMER_TYPE_TRIGGERS)
                .execute(ListType.of(IdNameDTO.class));
    }

    public CustomerTypes getCustomerTypes(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_CUSTOMER_TYPES)
                .pathParams(entityId)
                .execute(CustomerTypes.class);
    }

    public CustomerType getCustomerType(Long entityId, Long customerTypeId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_CUSTOMER_TYPE)
                .pathParams(entityId, customerTypeId)
                .execute(CustomerType.class);
    }

    public IdNameDTO createCustomerTypes(Long entityId, CustomerTypeCreateRequest request) {
        return httpClient.buildRequest(HttpMethod.POST, ENTITY_CUSTOMER_TYPES)
                .pathParams(entityId)
                .body(new ClientRequestBody(request))
                .execute(IdNameDTO.class);
    }

    public void updateCustomerTypes(Long entityId, Long customerTypeId, CustomerTypeUpdateRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_CUSTOMER_TYPE)
                .pathParams(entityId, customerTypeId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteCustomerTypes(Long entityId, Long customerTypeId) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITY_CUSTOMER_TYPE)
                .pathParams(entityId, customerTypeId)
                .execute();
    }

    public CustomerConfig getCustomerConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_CUSTOMER_CONFIG)
                .pathParams(entityId)
                .execute(CustomerConfig.class);
    }

    public void updateCustomerConfig(Long entityId, UpdateCustomerConfig customerConfig) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_CUSTOMER_CONFIG)
                .pathParams(entityId)
                .body(new ClientRequestBody(customerConfig))
                .execute();
    }


    public EntityLiterals getEntityLiterals(Long entityId, String languageCode) {
        return httpClient.buildRequest(HttpMethod.GET, CONTENTS_TEXT)
                .pathParams(entityId, languageCode)
                .execute(EntityLiterals.class);
    }

    public void createOrUpdateEntityLiterals(Long entityId, String languageCode, EntityLiterals body) {
        httpClient.buildRequest(HttpMethod.POST, CONTENTS_TEXT)
                .pathParams(entityId, languageCode)
                .body(new ClientRequestBody(body)).execute();
    }

    public EntityFriendsConfig getEntityFriendsConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_FRIENDS_CONFIG)
                .pathParams(entityId)
                .execute(EntityFriendsConfig.class);
    }

    public void updateEntityFriendsConfig(Long entityId, EntityFriendsConfig config) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_FRIENDS_CONFIG)
                .pathParams(entityId)
                .body(new ClientRequestBody(config))
                .execute();
    }

    public List<EntityGatewayConfig> getEntityGatewaysConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_GATEWAYS_CONFIG)
                .pathParams(entityId)
                .execute(ListType.of(EntityGatewayConfig.class));
    }
    
    public MemberCounter getMemberCounter(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_MEMBER_COUNTER)
            .pathParams(entityId)
            .execute(MemberCounter.class);
    }

    public void updateMemberCounter(Long entityId, UpdateMemberCounter request) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_MEMBER_COUNTER)
            .pathParams(entityId)
            .body(new ClientRequestBody(request))
            .execute();
    }
  
    public List<EntityTextBlock> getEntityTextBlocks(Long entityId, EntityTextBlockFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, CONTENTS_TEXT_BLOCKS).pathParams(entityId)
                .params(params.build())
                .execute(ListType.of(EntityTextBlock.class));
    }

    public void updateEntityTextBlocks(Long entityId, UpdateEntityTextBlocks body) {
        httpClient.buildRequest(HttpMethod.PUT, CONTENTS_TEXT_BLOCKS).pathParams(entityId)
                .body(new ClientRequestBody(body)).execute();
    }

    public List<EntityTextBlock> getEntityTemplateZoneTextBlocks(Long entityId, Long templateZoneId, EntityTextBlockFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, TEMPLATE_ZONES_CONTENTS_TEXT_BLOCKS)
                .pathParams(entityId, templateZoneId)
                .params(params.build())
                .execute(ListType.of(EntityTextBlock.class));
    }

    public void updateEntityTemplateZonesTextBlocks(Long entityId, Long templateZoneId, UpdateEntityTextBlocks body) {
        httpClient.buildRequest(HttpMethod.PUT, TEMPLATE_ZONES_CONTENTS_TEXT_BLOCKS)
                .pathParams(entityId, templateZoneId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public ResponseEntities getListOfEntities(EntitySearchFilter searchFilter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(searchFilter);
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES)
                .params(builder.build())
                .execute(ResponseEntities.class);
    }

    public EntityCommElementsTexts getEntityCommunicationElementsText(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_COMMUNICATION_ELEMENTS + "/texts")
                .pathParams(entityId)
                .execute(EntityCommElementsTexts.class);
    }

    public EntityCommElementsImages getEntityCommunicationElementsImages(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_COMMUNICATION_ELEMENTS + "/images")
                .pathParams(entityId)
                .execute(EntityCommElementsImages.class);
    }

    public EntityCommElementsTexts updateEntityCommunicationElementsText(Long entityId, EntityCommElementsTexts entityCommElementsTexts) {
        return httpClient.buildRequest(HttpMethod.PUT, ENTITY_COMMUNICATION_ELEMENTS + "/texts")
                .pathParams(entityId)
                .body(new ClientRequestBody(entityCommElementsTexts))
                .execute(EntityCommElementsTexts.class);
    }

    public void updateEntityCommunicationElementsImage(Long entityId, EntityCommElementsImages entityCommElementsImages) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_COMMUNICATION_ELEMENTS + "/images")
                .pathParams(entityId)
                .body(new ClientRequestBody(entityCommElementsImages))
                .execute();
    }

    public void deleteEntityCommunicationElementImage(Long entityId, String language, EntityImageContentType type) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITY_COMMUNICATION_ELEMENTS + "/images/languages/{language}/types/{type}")
                .pathParams(entityId, language, type)
                .execute();
    }

    public PhoneValidatorEntityConfig getPhoneValidatorEntityConfiguration(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_PHONE_VALIDATORS)
                .pathParams(entityId)
                .execute(PhoneValidatorEntityConfig.class);
    }

    public void updatePhoneValidatorEntityConfiguration(Long entityId, PhoneValidatorEntityConfig phoneValidatorEntityConfig) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_PHONE_VALIDATORS)
                .pathParams(entityId)
                .body(new ClientRequestBody(phoneValidatorEntityConfig))
                .execute();
    }

    public DomainSettings getCustomersDomainSettings(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_CUSTOMERS_DOMAIN_SETTINGS)
                .pathParams(entityId)
                .execute(DomainSettings.class);
    }

    public void upsertCustomersDomainSettings(Long entityId, DomainSettings requestBody) {
        httpClient.buildRequest(HttpMethod.POST, ENTITY_CUSTOMERS_DOMAIN_SETTINGS)
                .pathParams(entityId)
                .body(new ClientRequestBody(requestBody))
                .execute();
    }

    public void disableCustomersDomainSettings(Long entityId) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITY_CUSTOMERS_DOMAIN_SETTINGS)
                .pathParams(entityId)
                .execute();
    }

    public List<EntityBankAccount> getBankAccounts(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_BANK_ACCOUNTS)
                .pathParams(entityId)
                .execute(ListType.of(EntityBankAccount.class));
    }

    public EntityBankAccount getBankAccount(Long entityId, Long bankAccountId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY_BANK_ACCOUNT)
                .pathParams(entityId, bankAccountId)
                .execute(EntityBankAccount.class);
    }

    public IdDTO createBankAccount(EntityBankAccount bankAccount) {
        return httpClient.buildRequest(HttpMethod.POST, ENTITY_BANK_ACCOUNTS)
                .pathParams(bankAccount.getEntityId())
                .body(new ClientRequestBody(bankAccount))
                .execute(IdDTO.class);
    }

    public void updateBankAccount(EntityBankAccount bankAccount) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY_BANK_ACCOUNT)
                .pathParams(bankAccount.getEntityId(), bankAccount.getId())
                .body(new ClientRequestBody(bankAccount))
                .execute();
    }

    public void deleteBankAccount(Long entityId, Long bankAccountId) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITY_BANK_ACCOUNT)
                .pathParams(entityId, bankAccountId)
                .execute();
    }

    public TemplatesZonesResponse getTemplatesZones(Integer entityId, TemplatesZonesRequestFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return  httpClient.buildRequest(HttpMethod.GET, TEMPLATES_ZONES)
                .pathParams(entityId)
                .params(params.build())
                .execute(TemplatesZonesResponse.class);
    }

    public IdDTO createTemplateZones(Integer entityId, TemplatesZonesRequest request) {
        return  httpClient.buildRequest(HttpMethod.POST, TEMPLATES_ZONES)
                .pathParams(entityId)
                .body(new ClientRequestBody(request))
                .execute(IdDTO.class);
    }

    public TemplateZones getTemplateZones(Integer entityId, Integer templateZonesId) {
        return  httpClient.buildRequest(HttpMethod.GET, TEMPLATE_ZONES)
                .pathParams(entityId, templateZonesId)
                .execute(TemplateZones.class);
    }

    public void updateTemplateZones(Integer entityId, Integer templateZonesId, TemplatesZonesUpdateRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, TEMPLATE_ZONES)
                .pathParams(entityId, templateZonesId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteTemplateZones(Integer entityId, Integer templateZonesId) {
        httpClient.buildRequest(HttpMethod.DELETE, TEMPLATE_ZONES)
                .pathParams(entityId, templateZonesId)
                .execute();
    }

    public void updateUserVisibility(Long userId, UpdateVisibilityDTO request) {
        httpClient.buildRequest(HttpMethod.PUT, USER_VISIBILITY)
                .pathParams(userId)
                .body(new ClientRequestBody(request))
                .execute();
    }

}
