package es.onebox.flc.datasources.msvenue;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.flc.datasources.msvenue.dto.VenueDTO;
import es.onebox.flc.datasources.msvenue.dto.VenueStatus;
import es.onebox.flc.datasources.msvenue.dto.VenuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsVenueDatasource {
    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/venues-api/" + API_VERSION;

    private static final String ENTITY_ID = "entityId";
    private static final String OPERATOR_ID = "operatorId";
    private static final String STATUS = "status";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final HttpClient httpClient;

    @Autowired
    public MsVenueDatasource(@Value("${clients.services.ms-venue}") String baseUrl, ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .build();
    }

    public VenueDTO getVenue(Long venueId) {
        return httpClient.buildRequest(HttpMethod.GET, "/venues/{venueId}")
                .pathParams(venueId)
                .execute(VenueDTO.class);
    }

    public VenuesDTO getVenues(Integer entityId, Integer operatorId, Boolean inUse, Long limit, Long offset) {
        Map<String, Object> params = new HashMap<>();
        params.put(ENTITY_ID, entityId);
        params.put(OPERATOR_ID, operatorId);
        if (inUse) {
            params.put(STATUS, VenueStatus.ACTIVE);
        }
        params.put(LIMIT, limit);
        params.put(OFFSET, offset);

        return httpClient.buildRequest(HttpMethod.GET, "/venues")
                .params(DatasourceUtils.prepareQueryParams(params))
                .execute(VenuesDTO.class);
    }
}