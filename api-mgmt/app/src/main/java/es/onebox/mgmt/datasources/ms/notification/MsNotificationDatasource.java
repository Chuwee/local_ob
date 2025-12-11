package es.onebox.mgmt.datasources.ms.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.notification.dto.CreateNotificationConfig;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfig;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfigs;
import es.onebox.mgmt.datasources.ms.notification.dto.SearchNotificationConfigFilter;
import es.onebox.mgmt.datasources.ms.notification.dto.UpdateNotificationConfig;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.notifications.enums.NotificationSortableField;
import es.onebox.tracer.okhttp.TracingInterceptor;
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

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("500MN0001", ApiMgmtErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("NOT_FOUND", ApiMgmtErrorCode.NOT_FOUND);
        ERROR_CODES.put("ENTITY_NOT_FOUND", ApiMgmtErrorCode.ENTITY_NOT_FOUND);
        ERROR_CODES.put("FORBIDDEN", ApiMgmtErrorCode.FORBIDDEN_RESOURCE);
        ERROR_CODES.put("REQUIRED_PARAMS", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("BAD_PARAMETER", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("TOO_MANY_RESULTS", ApiMgmtErrorCode.TOO_MANY_RESULTS);
        ERROR_CODES.put("GENERIC_DATABASE_ERROR", ApiMgmtErrorCode.PERSISTENCE_ERROR);
        ERROR_CODES.put("ENTITY_CONFIG_CREATE_CONFLICT", ApiMgmtErrorCode.ENTITY_CONFIG_CREATE_CONFLICT);
        ERROR_CODES.put("ENTITY_CONFIG_NOT_FOUND", ApiMgmtErrorCode.ENTITY_CONFIG_NOT_FOUND);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsNotificationDatasource(@Value("${clients.services.ms-notification}") String baseUrl,
                                    ObjectMapper jacksonMapper,
                                    TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public NotificationConfig getNotificationConfig(String documentId){
        return httpClient.buildRequest(HttpMethod.GET, WEBHOOK)
                .pathParams(documentId)
                .execute(NotificationConfig.class);
    }

    public NotificationConfigs searchNotificationConfigs(SearchNotificationConfigFilter filter){
        QueryParameters.Builder builder = new QueryParameters.Builder();

        ConverterUtils.checkSortFields(filter.getSort(), builder, NotificationSortableField::byName);
        if (filter.getSort() != null) {
            String value = filter.getSort().getSortDirections().stream()
                .findFirst()
                .map(SortDirection::getValue)
                .orElse(null);

            if (value != null && (value.toLowerCase().equals(NotificationSortableField.ENTITY.getDtoName()) || value.toLowerCase().equals(NotificationSortableField.OPERATOR.getDtoName()))) {
                builder.removeQueryParameter("sort");
            }

            filter.setSort(null);
        }


        builder.addQueryParameters(filter);

        return httpClient.buildRequest(HttpMethod.GET, WEBHOOKS)
                .params(builder.build())
                .execute(NotificationConfigs.class);
    }

    public NotificationConfig createNotificationConfig(CreateNotificationConfig createDTO){
        return httpClient.buildRequest(HttpMethod.POST, WEBHOOKS)
                .body(new ClientRequestBody(createDTO))
                .execute(NotificationConfig.class);
    }

    public void updateNotificationConfig(String documentId, UpdateNotificationConfig updateDTO){
        httpClient.buildRequest(HttpMethod.PUT, WEBHOOK)
                .pathParams(documentId)
                .body(new ClientRequestBody(updateDTO))
                .execute();
    }

    public void deleteNotificationConfig(String documentId){
        httpClient.buildRequest(HttpMethod.DELETE, WEBHOOK)
                .pathParams(documentId)
                .execute();
    }

    public NotificationConfig regenerateApiKey(String documentId){
        return httpClient.buildRequest(HttpMethod.PUT, WEBHOOK + "/apikey/regenerate")
                .pathParams(documentId)
                .execute(NotificationConfig.class);
    }
}
