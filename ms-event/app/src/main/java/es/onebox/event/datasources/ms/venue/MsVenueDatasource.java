package es.onebox.event.datasources.ms.venue;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.event.datasources.ms.venue.dto.BlockingReason;
import es.onebox.event.datasources.ms.venue.dto.CapacityMapDTO;
import es.onebox.event.datasources.ms.venue.dto.CreateVenueTemplate;
import es.onebox.event.datasources.ms.venue.dto.Gate;
import es.onebox.event.datasources.ms.venue.dto.PriceTypeCommunicationElement;
import es.onebox.event.datasources.ms.venue.dto.PriceTypeRequest;
import es.onebox.event.datasources.ms.venue.dto.QuotaDTO;
import es.onebox.event.datasources.ms.venue.dto.SectorDTO;
import es.onebox.event.datasources.ms.venue.dto.UpdateVenueTemplateRequest;
import es.onebox.event.datasources.ms.venue.dto.Venue;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplateType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsVenueDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/venues-api/" + API_VERSION;

    private static final String VENUES = "/venues";
    private static final String VENUE_ID = "/{venueId}";

    private static final String VENUE_TEMPLATES = "/venue-templates";
    private static final String VENUE_TEMPLATE = VENUE_TEMPLATES + "/{id}";
    private static final String PRICE_TYPES = VENUE_TEMPLATE + "/priceTypes";
    private static final String PRICE_TYPE = PRICE_TYPES + "/{priceTypeId}";
    private static final String GATES = VENUE_TEMPLATE + "/gates";
    private static final String SECTORS = VENUE_TEMPLATE + "/sectors";
    private static final String QUOTAS = VENUE_TEMPLATE + "/quotas";
    private static final String COMM_ELEMENTS = PRICE_TYPE + "/web-communication-elements";

    private final HttpClient httpClient;

    @Autowired
    public MsVenueDatasource(@Value("${clients.services.ms-venue}") String baseUrl,
                             ObjectMapper jacksonMapper,
                             TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .readTimeout(60000L)
                .build();
    }

    public void upsertPriceTypeCommElements(Long venueTemplateId, Long priceTypeId,
                                            List<PriceTypeCommunicationElement> commElements) {
        httpClient.buildRequest(HttpMethod.POST, COMM_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(commElements))
                .execute();
    }

    public void deletePriceTypeCommElements(Long venueTemplateId, Long priceTypeId) {
        httpClient.buildRequest(HttpMethod.DELETE, COMM_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .execute();
    }

    public Venue getVenue(Long venueId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUES + VENUE_ID)
                .pathParams(venueId)
                .execute(Venue.class);
    }

    public VenueTemplate getVenueTemplate(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE)
                .pathParams(venueTemplateId)
                .execute(VenueTemplate.class);
    }

    public List<Gate> getGates(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, GATES)
                .pathParams(venueTemplateId)
                .execute(ListType.of(Gate.class));
    }

    public List<BlockingReason> getBlockingReasons(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/blockingReasons")
                .pathParams(venueTemplateId)
                .execute(ListType.of(BlockingReason.class));
    }

    public CapacityMapDTO getVenueCapacityMap(final Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATES + "/{sessionId}/capacity")
                .pathParams(sessionId)
                .execute(CapacityMapDTO.class);
    }

    public List<QuotaDTO> getQuotas(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, QUOTAS)
                .pathParams(venueTemplateId)
                .execute(ListType.of(QuotaDTO.class));
    }

    public Long createVenueTemplate(String name, Long eventId, Long venueId, Long spaceId,
                                    Long entityId, VenueTemplateType type, Boolean smartBooking) {

        CreateVenueTemplate createVenueTemplate = new CreateVenueTemplate();
        createVenueTemplate.setName(name);
        createVenueTemplate.setEventId(eventId);
        createVenueTemplate.setVenueId(venueId);
        createVenueTemplate.setSpaceId(spaceId);
        createVenueTemplate.setEntityId(entityId);
        createVenueTemplate.setType(type);
        createVenueTemplate.setSmartBooking(smartBooking);

        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATES)
                .pathParams(eventId)
                .body(new ClientRequestBody(createVenueTemplate))
                .execute(IdDTO.class).getId();
    }

    public void updateVenueTemplate(Long venueTemplateId, UpdateVenueTemplateRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public List<IdNameCodeDTO> getPriceTypes(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, PRICE_TYPES)
                .pathParams(venueTemplateId)
                .execute(ListType.of(IdNameCodeDTO.class));
    }

    public Long createPriceType(Long venueTemplateId, PriceTypeRequest requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, PRICE_TYPES)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(requestDTO))
                .execute(IdDTO.class).getId();
    }

    public void updatePriceType(Long venueTemplateId, Long priceTypeId, PriceTypeRequest requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, PRICE_TYPE)
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public List<SectorDTO> getSectorsByTemplateId(Integer venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, SECTORS)
                .pathParams(venueTemplateId)
                .execute(ListType.of(SectorDTO.class));
    }
}
