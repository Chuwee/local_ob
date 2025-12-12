package es.onebox.ms.notification.datasources.ms.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.ms.notification.webhooks.dto.ExternalApiWebhookDto;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiExternalDatasource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExternalDatasource.class);
    private static final String API_EXTERNAL_WEBHOOK = "/internal-api/v1/sgtm/webhook";

    private HttpClient httpClient;


    @Autowired
    public ApiExternalDatasource(@Value("${clients.services.api-external}") String baseUrl,
                                 ObjectMapper jacksonMapper,
                                 TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .build();
    }

    public void sendNotificationToApiExternal(Long channelId, RequestHeaders headers, ExternalApiWebhookDto activeExternalTools) {

        List<Long> channelIds = List.of(channelId);
        QueryParameters query = new QueryParameters.Builder()
                .addQueryParameter("channelId", channelIds)
                .build();
        try {
            httpClient.buildRequest(HttpMethod.POST, API_EXTERNAL_WEBHOOK)
                    .params(query)
                    .headers(headers)
                    .body(new ClientRequestBody(activeExternalTools))
                    .execute();
        } catch (Exception e) {
            LOGGER.error("[WEBHOOK] Error sending webhook to api-external");
            throw e;
        }

    }
}
