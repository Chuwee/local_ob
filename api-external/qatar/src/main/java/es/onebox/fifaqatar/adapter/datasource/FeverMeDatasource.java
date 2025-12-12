package es.onebox.fifaqatar.adapter.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.audit.okhttp.AuditTracingInterceptor;
import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.fifaqatar.adapter.datasource.dto.MeResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeverMeDatasource {

    private static final String BASE_PATH = "/api/4.2/users/me/";


    private final HttpClient httpClient;

    public FeverMeDatasource(@Value("${fifa-qatar.me-endpoint.url}") String url,
                             AuditTracingInterceptor tracingInterceptor) {
        ObjectMapper objectMapper = JsonMapper.jacksonMapper();
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(url + BASE_PATH)
                .interceptors(tracingInterceptor)
                .jacksonMapper(objectMapper)
                .build();
    }

    @Cached(key = "fv_me")
    public MeResponseDTO me(@CachedArg String accessToken) {
        return httpClient.buildRequest(HttpMethod.GET, "")
                .headers(DatasourceUtils.prepareAuthHeader(accessToken))
                .execute(MeResponseDTO.class);
    }
}
