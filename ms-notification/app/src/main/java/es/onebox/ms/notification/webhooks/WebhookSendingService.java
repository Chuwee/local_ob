package es.onebox.ms.notification.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.ms.notification.common.utils.GeneratorUtils;
import es.onebox.ms.notification.datasources.repository.ApiExternalRepository;
import es.onebox.ms.notification.webhooks.dto.ExternalApiWebhookDto;
import es.onebox.ms.notification.webhooks.dto.NotificationMessageDTO;
import es.onebox.ms.notification.webhooks.enums.OrderAction;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebhookSendingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookSendingService.class);


    private final HttpClient httpClient;
    private final ApiExternalRepository apiExternalRepository;

    @Autowired
    public WebhookSendingService(ObjectMapper jacksonMapper,
                                 TracingInterceptor tracingInterceptor,
                                 ApiExternalRepository apiExternalRepository) {
        this.apiExternalRepository = apiExternalRepository;
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl("")
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .build();
    }

    public boolean sendNotification(String url, NotificationMessageDTO messageDTO) {
        String deliveryId = GeneratorUtils.generateUUID();
        LOGGER.info("[WEBHOOK] [{}] [{}] Sending message {} to {}", messageDTO.getCode(), deliveryId, messageDTO.getPayload(), url);
        try {
            RequestHeaders.Builder requestHeaders = new RequestHeaders.Builder();
            requestHeaders.addHeader("ob-action", messageDTO.getAction())
                    .addHeader("ob-delivery-id", deliveryId)
                    .addHeader("ob-event", messageDTO.getEvent())
                    .addHeader("ob-hook-id", messageDTO.getConfId())
                    .addHeader("ob-signature", messageDTO.getSignature());
            if (messageDTO.getNotificationSubtype() != null && !messageDTO.getNotificationSubtype().isEmpty()) {
                requestHeaders.addHeader("ob-subtype", messageDTO.getNotificationSubtype());
            }
            httpClient.buildRequest(HttpMethod.POST, url)
                    .headers(requestHeaders.build())
                    .body(new ClientRequestBody(messageDTO.getPayload()))
                    .execute();
        } catch (Exception e) {
            LOGGER.error("[WEBHOOK] [{}] Error sending message {} to {}", messageDTO.getCode(), messageDTO.getPayload(), url);
            throw e;
        }

        if (messageDTO.getPrevCode() != null) {
            try {
                httpClient.buildRequest(HttpMethod.POST, url)
                        .headers(new RequestHeaders.Builder()
                                .addHeader("ob-action", OrderAction.UPDATE.name())
                                .addHeader("ob-delivery-id", deliveryId)
                                .addHeader("ob-event", messageDTO.getEvent())
                                .addHeader("ob-hook-id", messageDTO.getConfId())
                                .addHeader("ob-signature", messageDTO.getPrevSignature()).build())
                        .body(new ClientRequestBody(messageDTO.getPrevPayload()))
                        .execute();
            } catch (Exception e) {
                LOGGER.error("[WEBHOOK] [{}] Error sending previous message {} to {}", messageDTO.getPrevCode(), messageDTO.getPrevPayload(), url);
                throw e;

            }
        }
        return true;
    }

    public void sendNotificationToApiExternal(Long channelId, RequestHeaders headers, ExternalApiWebhookDto activeExternalTools) {
        apiExternalRepository.sendNotificationToApiExternal(channelId, headers, activeExternalTools);
    }
}
