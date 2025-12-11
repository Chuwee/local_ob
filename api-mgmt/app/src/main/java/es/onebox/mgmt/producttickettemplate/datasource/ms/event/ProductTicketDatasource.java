package es.onebox.mgmt.producttickettemplate.datasource.ms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.datasources.ms.event.MsEventMapping;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.CreateProductTicketTemplateRequest;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketModelResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateFilter;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateLanguages;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplatePageResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.UpdateProductTicketTemplateRequest;
import es.onebox.mgmt.tickettemplates.dto.CloneTemplateRequest;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductTicketDatasource extends MsEventMapping {

	private final HttpClient httpClient;

	private static final String PRODUCT_TICKET_TEMPLATES = "/product-ticket-templates";
	private static final String PRODUCT_TICKET_MODELS = "/models";
	private static final String PRODUCT_TICKET_TEMPLATE_ID = "/{productTicketTemplateId}";
	private static final String PRODUCT_TICKET_TEMPLATE_LANGUAGES = PRODUCT_TICKET_TEMPLATES + PRODUCT_TICKET_TEMPLATE_ID + "/languages";
	protected static final String PRODUCT_TICKET_TEMPLATE_CLONE = PRODUCT_TICKET_TEMPLATES + PRODUCT_TICKET_TEMPLATE_ID + "/clone";
	private static final String BASE_PATH = "/events-api/v1";
	private static final int TIMEOUT = 60000;

	public ProductTicketDatasource(@Value("${clients.services.ms-event}") String baseUrl,
			ObjectMapper jacksonMapper,
			TracingInterceptor tracingInterceptor) {
		this.httpClient = HttpClientFactoryBuilder.builder()
				.baseUrl(baseUrl + BASE_PATH)
				.jacksonMapper(jacksonMapper)
				.interceptors(tracingInterceptor)
				.exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES,
						jacksonMapper))
				.readTimeout(TIMEOUT)
				.build();
	}

	public ProductTicketTemplatePageResponse search(ProductTicketTemplateFilter filter) {
		QueryParameters.Builder params = new QueryParameters.Builder();
		params.addQueryParameters(filter);
		return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_TEMPLATES)
				.params(params.build())
				.execute(ProductTicketTemplatePageResponse.class);
	}

	public ProductTicketTemplateResponse getById(Long productTicketTemplateId) {
		return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_TEMPLATES + PRODUCT_TICKET_TEMPLATE_ID)
				.pathParams(productTicketTemplateId)
				.execute(ProductTicketTemplateResponse.class);
	}

	public Long create(CreateProductTicketTemplateRequest createRequest) {
		return httpClient.buildRequest(HttpMethod.POST, PRODUCT_TICKET_TEMPLATES)
				.body(new ClientRequestBody(createRequest))
				.execute(IdDTO.class).getId();
	}

	public void update(Long productTicketTemplateId, UpdateProductTicketTemplateRequest updateRequest) {
		httpClient.buildRequest(HttpMethod.PUT, PRODUCT_TICKET_TEMPLATES + PRODUCT_TICKET_TEMPLATE_ID)
				.pathParams(productTicketTemplateId)
				.body(new ClientRequestBody(updateRequest))
				.execute();
	}

	public void delete(Long productTicketTemplateId) {
		httpClient.buildRequest(HttpMethod.DELETE, PRODUCT_TICKET_TEMPLATES + PRODUCT_TICKET_TEMPLATE_ID)
				.pathParams(productTicketTemplateId)
				.execute();
	}

	public List<ProductTicketModelResponse> getAllModels() {
		return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_TEMPLATES + PRODUCT_TICKET_MODELS)
				.execute(ListType.of(ProductTicketModelResponse.class));
	}

	public Long cloneProductTicketTemplate(Long templateId, CloneTemplateRequest body) {
		return httpClient.buildRequest(HttpMethod.POST, PRODUCT_TICKET_TEMPLATE_CLONE).pathParams(templateId)
				.body(new ClientRequestBody(body)).execute(IdDTO.class).getId();
	}

	public ProductTicketTemplateLanguages getProductTicketTemplateLanguages(Long templateId) {
		return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_TEMPLATE_LANGUAGES)
				.pathParams(templateId)
				.execute(ProductTicketTemplateLanguages.class);
	}
}
