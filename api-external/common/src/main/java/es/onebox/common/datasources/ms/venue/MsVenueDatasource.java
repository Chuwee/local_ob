package es.onebox.common.datasources.ms.venue;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.venue.dto.BasePriceType;
import es.onebox.common.datasources.ms.venue.dto.BlockingReasonDTO;
import es.onebox.common.datasources.ms.venue.dto.CreateVenueTemplateTag;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeDTO;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeWebCommunicationElementDTO;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeCommunicationElementFilter;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeRequestDTO;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeTicketCommunicationElement;
import es.onebox.common.datasources.ms.venue.dto.SectorDTO;
import es.onebox.common.datasources.ms.venue.dto.VenueDTO;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplateFilter;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplates;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplatesDTO;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplatesFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("msVenueDataSource")
public class MsVenueDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/venues-api/" + API_VERSION;

    private static final String VENUE_TEMPLATES = "/venue-templates";
    private static final String VENUE_TEMPLATE = VENUE_TEMPLATES + "/{id}";
    private static final String VENUE_TEMPLATE_ID = "/{venueTemplateId}";
    private static final String TICKET_COMMUNICATION_ELEMENTS = VENUE_TEMPLATES + VENUE_TEMPLATE_ID + "/priceTypes/{priceTypeId}/pdf-communication-elements";
    private static final String WEB_COMMUNICATION_ELEMENTS = VENUE_TEMPLATES + VENUE_TEMPLATE_ID + "/priceTypes/{priceTypeId}/web-communication-elements";
    private static final String BLOCKING_REASONS = VENUE_TEMPLATES + VENUE_TEMPLATE_ID + "/blockingReasons";
    private static final String PRICE_TYPE = "/priceTypes/{priceTypeId}";

    private final HttpClient httpClient;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("400G0001", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("VENUE_TEMPLATE_NOT_FOUND", ApiExternalErrorCode.VENUE_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND", ApiExternalErrorCode.VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND);
    }

    @Autowired
    public MsVenueDatasource(@Value("${clients.services.ms-venue}") String baseUrl,
                             ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(15000L)
                .build();
    }

    public VenueTemplate getVenueTemplate(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE)
                .pathParams(id)
                .execute(VenueTemplate.class);
    }

    public VenueTemplates getVenueTemplates(Long operatorId, VenueTemplatesFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder().addQueryParameters(filter);
        params.addQueryParameter("operatorId", String.valueOf(operatorId));

        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATES)
                .params(params.build())
                .execute(VenueTemplates.class);
    }

    public List<BasePriceType> getPriceTypes(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/priceTypes")
                .pathParams(venueTemplateId)
                .execute(ListType.of(BasePriceType.class));
    }

    public List<SectorDTO> getSectors(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/sectors")
                .pathParams(venueTemplateId)
                .execute(ListType.of(SectorDTO.class));
    }

    public void updatePriceType(Long venueTemplateId, Long priceTypeId, PriceTypeRequestDTO requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/priceTypes/{priceTypeId}")
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute();
    }

    private CreateVenueTemplateTag buildTagRequestBody(PriceTypeRequestDTO requestDTO) {
        return new CreateVenueTemplateTag(requestDTO.getName(), requestDTO.getCode(), requestDTO.getColor(),
                requestDTO.getPriority(), null, requestDTO.getPriceTypeAdditionalConfigDTO());
    }

    public List<PriceTypeTicketCommunicationElement> getPriceTypeTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, PriceTypeCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(PriceTypeTicketCommunicationElement.class));
    }

    public VenueDTO getVenue(Long venueId) {
        return httpClient.buildRequest(HttpMethod.GET, "/venues/{venueId}")
                .pathParams(venueId)
                .execute(VenueDTO.class);
    }

    public VenueTemplatesDTO getVenueByEventId(Long eventId) {
        VenueTemplateFilter filter = new VenueTemplateFilter();
        filter.setEventId(Math.toIntExact(eventId));
        return httpClient.buildRequest(HttpMethod.GET, "/venue-templates")
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(VenueTemplatesDTO.class);
    }

    public List<BlockingReasonDTO> getBlockingReasons(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, BLOCKING_REASONS)
                .pathParams(venueTemplateId)
                .execute(ListType.of(BlockingReasonDTO.class));
    }

    public MsPriceTypeDTO getPriceType(Long venueTemplateId, Long priceTypeId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATES + VENUE_TEMPLATE_ID + PRICE_TYPE)
                .pathParams(venueTemplateId, priceTypeId)
                .execute(MsPriceTypeDTO.class);
    }

    public List<MsPriceTypeWebCommunicationElementDTO> getPriceTypeWebCommunicationElements(Long venueTemplateId, Long priceTypeId, PriceTypeCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, WEB_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(MsPriceTypeWebCommunicationElementDTO.class));
    }
}
