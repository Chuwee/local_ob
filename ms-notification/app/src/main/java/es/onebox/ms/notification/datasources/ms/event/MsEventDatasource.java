package es.onebox.ms.notification.datasources.ms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.ms.notification.datasources.ms.event.dto.Event;
import es.onebox.ms.notification.datasources.ms.event.dto.Product;
import es.onebox.ms.notification.datasources.ms.event.dto.Session;
import es.onebox.ms.notification.datasources.ms.event.dto.Sessions;
import es.onebox.ms.notification.exception.ClientHttpExceptionBuilder;
import okhttp3.Interceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MsEventDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/events-api/" + API_VERSION;

    private static final String SESSION = "/sessions/{sessionId}";
    private static final String EVENT = "/events/{eventId}";
    private static final String PRODUCT = "/products/{productId}";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();


    @Autowired
    public MsEventDatasource(@Value("${clients.services.ms-event}") String baseUrl,
                             ObjectMapper jacksonMapper,
                             Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .connectTimeout(2000)
                .readTimeout(10000)
                .build();
    }

    public Event getEvent(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT)
                .pathParams(id)
                .execute(Event.class);
    }

    public Session getSession(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION)
                .pathParams(id)
                .execute(Session.class);
    }

    public Sessions getSessions(List<Long> sessionIds) {
        String ids = StringUtils.join(sessionIds, ",");
        return httpClient.buildRequest(HttpMethod.GET, "/sessions")
                .params(new QueryParameters.Builder().addQueryParameter("ids", ids).build())
                .execute(Sessions.class);
    }

    public Product getProduct(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT)
                .pathParams(id)
                .execute(Product.class);
    }

}
