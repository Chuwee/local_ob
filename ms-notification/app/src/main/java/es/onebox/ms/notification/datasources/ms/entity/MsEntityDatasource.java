package es.onebox.ms.notification.datasources.ms.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entities;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entity;
import es.onebox.ms.notification.datasources.ms.entity.dto.ExternalMgmtConfig;
import es.onebox.ms.notification.datasources.ms.entity.dto.SearchEntitiesFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MsEntityDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-entity-api/" + API_VERSION;

    private final HttpClient httpClient;

    @Autowired
    public MsEntityDatasource(@Value("${clients.services.ms-entity}") String baseUrl,
                              ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .build();
    }

    public Entity getEntity(Integer entityId) {
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}")
                .pathParams(entityId)
                .execute(Entity.class);
    }

    public Entities getEntities(SearchEntitiesFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, "/entities")
                .params(builder.build())
                .execute(Entities.class);
    }

    public List<ExternalMgmtConfig> getExternalMgmtConfig(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, "/entities/{entityId}/external-management")
                .pathParams(entityId)
                .execute(ListType.of(ExternalMgmtConfig.class));
    }
}
