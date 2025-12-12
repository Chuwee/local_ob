package es.onebox.ms.notification.datasources.repository;

import es.onebox.cache.annotation.CachedArg;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.ms.notification.datasources.ms.channel.ApiExternalDatasource;
import es.onebox.ms.notification.webhooks.dto.ExternalApiWebhookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApiExternalRepository {
    private final ApiExternalDatasource apiExternalDatasource;

    @Autowired
    public ApiExternalRepository(ApiExternalDatasource apiExternalDatasource) {
        this.apiExternalDatasource = apiExternalDatasource;
    }


    public void sendNotificationToApiExternal(@CachedArg Long channelId, RequestHeaders headers, ExternalApiWebhookDto activeExternalTools) {
        apiExternalDatasource.sendNotificationToApiExternal(channelId, headers, activeExternalTools);
    }
}
