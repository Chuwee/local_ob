package es.onebox.common.datasources.mappings;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.mappings.dto.MappingResponse;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IntMappingsDatasource {

    private static final String API_VERSION = "1.0";
    private static final String BASE_PATH = "/int-mappings-api/" + API_VERSION;
    private static final String ID_SEPARATOR = "%3A%3A";

    private final HttpClient httpClient;
    private static final long CONNECTION_TIMEOUT = 10000L;

    @Autowired
    public IntMappingsDatasource(@Value("${clients.services.int-mapping-service}") String baseUrl,
                                 ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .build();
    }

    public MappingResponse getOBSeatId(Long entityId, Integer capacityId, Integer matchId, Long externalSeatId) {
        String id = capacityId + ID_SEPARATOR + matchId + ID_SEPARATOR + externalSeatId;
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}/attributes/TICKET/onebox-mappings/{id}")
                .pathParams(entityId, id)
                .execute(MappingResponse.class);
    }

}
