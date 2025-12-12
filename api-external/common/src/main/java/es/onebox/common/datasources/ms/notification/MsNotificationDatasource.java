package es.onebox.common.datasources.ms.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.notification.dto.NotificationConfigDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsNotificationDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-notification-api/" + API_VERSION;

    private static final String WEBHOOKS = "/webhooks";
    private static final String WEBHOOK = "/webhooks/{documentId}";

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("NOT_FOUND", ApiExternalErrorCode.NOT_FOUND);
        ERROR_CODES.put("ENTITY_NOT_FOUND", ApiExternalErrorCode.ENTITY_NOT_FOUND);
        ERROR_CODES.put("FORBIDDEN", ApiExternalErrorCode.FORBIDDEN_RESOURCE);
        ERROR_CODES.put("REQUIRED_PARAMS", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("BAD_PARAMETER", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("TOO_MANY_RESULTS", ApiExternalErrorCode.TOO_MANY_RESULTS);
        ERROR_CODES.put("GENERIC_DATABASE_ERROR", ApiExternalErrorCode.PERSISTENCE_ERROR);
        ERROR_CODES.put("ENTITY_CONFIG_CREATE_CONFLICT", ApiExternalErrorCode.ENTITY_CONFIG_CREATE_CONFLICT);
        ERROR_CODES.put("ENTITY_CONFIG_NOT_FOUND", ApiExternalErrorCode.ENTITY_CONFIG_NOT_FOUND);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsNotificationDatasource(@Value("${clients.services.ms-notification}") String baseUrl,
                                    ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public NotificationConfigDTO getNotificationConfig(String documentId) {
        return httpClient.buildRequest(HttpMethod.GET, WEBHOOK)
                .pathParams(documentId)
                .execute(NotificationConfigDTO.class);
    }
}
