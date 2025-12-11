package es.onebox.event.datasources.ms.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.event.datasources.ms.entity.dto.Attribute;
import es.onebox.event.datasources.ms.entity.dto.AttributeScope;
import es.onebox.event.datasources.ms.entity.dto.CountryDTO;
import es.onebox.event.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.event.datasources.ms.entity.dto.CurrencyDTO;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypeSearchFilter;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.datasources.ms.entity.dto.EntityConfigDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.ExternalBarcodeEntityConfigDTO;
import es.onebox.event.datasources.ms.entity.dto.ExternalEntityConfig;
import es.onebox.event.datasources.ms.entity.dto.ExternalLoginConfig;
import es.onebox.event.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.event.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.event.datasources.ms.entity.dto.OperatorCurrenciesDTO;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.dto.ProducerInvoiceProvider;
import es.onebox.event.datasources.ms.entity.dto.ProducersDTO;
import es.onebox.event.datasources.ms.entity.dto.request.ProducersRequest;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MsEntityDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-entity-api/" + API_VERSION;

    private static final String ENTITIES = "/entities";
    private static final String PRODUCERS = "/producers";
    private static final String MASTERDATA = "/masterdata";
    private static final String CURRENCIES = MASTERDATA + "/currencies";
    private static final String PRODUCER = PRODUCERS + "/{producerId}";
    private static final String INVOICE_PREFIXES = PRODUCER + "/invoice-prefixes";
    private static final String INVOICE_PREFIX = INVOICE_PREFIXES + "/{invoicePrefixId}";
    private static final String EXTERNAL_LOGIN_CONFIG = "/external-login-config";
    private static final String EXTERNAL_LOGIN_BY_PROVIDER = EXTERNAL_LOGIN_CONFIG + "/{provider}";

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("MTK0003", null);
        ERROR_CODES.put("PRODUCER_NOT_FOUND", MsEventSessionErrorCode.PRODUCER_NOT_FOUND);
        ERROR_CODES.put("INVOICE_PREFIX_NOT_FOUND", MsEventSessionErrorCode.INVOICE_PREFIX_NOT_FOUND);
    }

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

    public EntityDTO getEntity(Integer entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}")
                .pathParams(entityId)
                .execute(EntityDTO.class);
    }

    public EntityConfigDTO getEntityConfig(Integer entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/config")
                .pathParams(entityId)
                .execute(EntityConfigDTO.class);
    }

    public ExternalBarcodeEntityConfigDTO getEntityExternalBarcodeConfig(Integer entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/externalBarcodeConfig")
                .pathParams(entityId)
                .execute(ExternalBarcodeEntityConfigDTO.class);
    }

    public List<Attribute> getAttributes(Long entityId, AttributeScope attributeScope) {
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameter("scope", attributeScope.getId())
                .build();

        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/attributes")
                .pathParams(entityId)
                .params(query)
                .execute(ListType.of(Attribute.class));
    }

    public ProducerDTO getProducer(Integer entityId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCERS + "/{producerId}")
                .pathParams(entityId)
                .execute(ProducerDTO.class);
    }

    public ProducersDTO getProducers(ProducersRequest request) {
        var params = new QueryParameters.Builder().addQueryParameters(request).build();
        return httpClient.buildRequest(HttpMethod.GET, PRODUCERS)
                .params(params)
                .execute(ProducersDTO.class);
    }

    public ProducerInvoiceProvider getProducerInvoiceProvider(Integer producerId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCERS + "/{producerId}/invoice-providers")
                .pathParams(producerId)
                .execute(ProducerInvoiceProvider.class);
    }

    public CountryDTO getCountry(Integer countryId) {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/countries/{countryId}")
                .pathParams(countryId)
                .execute(CountryDTO.class);
    }

    public CountrySubdivisionDTO getCountrySubdivision(Integer countrySubdivisionId) {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/countries/subdivision/{countryId}")
                .pathParams(countrySubdivisionId)
                .execute(CountrySubdivisionDTO.class);
    }

    public List<MasterdataValue> getAllLanguages() {
        return httpClient.buildRequest(HttpMethod.GET, MASTERDATA + "/languages")
                .execute(ListType.of(MasterdataValue.class));
    }

    public InvoicePrefix getInvoicePrefix(Integer producerId, Integer invoicePrefixId) {
        return httpClient.buildRequest(HttpMethod.GET, INVOICE_PREFIX)
                .pathParams(producerId, invoicePrefixId)
                .execute(InvoicePrefix.class);
    }

    public InvoicePrefix getInvoicePrefix(Integer invoicePrefixId) {
        return httpClient.buildRequest(HttpMethod.GET, "/invoice-prefixes/{invoicePrefixId}")
                .pathParams(invoicePrefixId)
                .execute(InvoicePrefix.class);
    }

    public ExternalEntityConfig getExternalEntityConfig(Integer entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITIES + "/{entityId}/external-config")
                .pathParams(entityId)
                .execute(ExternalEntityConfig.class);
    }

    public OperatorCurrenciesDTO getOperatorCurrencies(Integer operatorId) {
        return httpClient.buildRequest(HttpMethod.GET, "/operators/{operatorId}/currencies")
                .pathParams(operatorId)
                .execute(OperatorCurrenciesDTO.class);
    }

    public CustomerTypes getCustomerTypes(Integer entityId, CustomerTypeSearchFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}/customer-types")
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .pathParams(entityId)
                .execute(CustomerTypes.class);
    }

    public List<CurrencyDTO> getCurrencies() {
        return httpClient.buildRequest(HttpMethod.GET, CURRENCIES)
                .execute(ListType.of(CurrencyDTO.class));
    }

	public ExternalLoginConfig getExternalLoginConfig(Provider provider) {

		return httpClient.buildRequest(HttpMethod.GET, ENTITIES + EXTERNAL_LOGIN_BY_PROVIDER)
				.pathParams(provider)
                .execute(ExternalLoginConfig.class);
	}
}
