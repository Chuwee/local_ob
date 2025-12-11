package es.onebox.event.datasources.ms.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.event.datasources.ms.notification.dto.ExternalNotification;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.event.exception.MsEventSessionErrorCode;
import okhttp3.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MsNotificationDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/notifications-api/" + API_VERSION;
    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("PRODUCER_NOT_FOUND", MsEventSessionErrorCode.PRODUCER_ID_NOT_VALID);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsNotificationDatasource(@Value("${clients.services.ms-notification}") String baseUrl,
                                    ObjectMapper jacksonMapper,
                                    Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public List<ExternalNotification> getExternalNotifications() {
        return httpClient.buildRequest(HttpMethod.GET, "/externalNotifications")
                .execute(ListType.of(ExternalNotification.class));
    }

}
