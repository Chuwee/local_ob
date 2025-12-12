package es.onebox.ms.notification.providerplansettings;

import es.onebox.datasource.http.RequestHeaders;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.ms.notification.webhooks.WebhookSendingService;
import es.onebox.ms.notification.webhooks.dto.ExternalApiWebhookDto;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderPlanSettingsNotificationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderPlanSettingsNotificationProcessor.class);

    @Autowired
    private WebhookSendingService webhookSendingService;

    @Override
    public void execute(Exchange exchange) {
        ProviderPlanSettingsNotificationMessage message = exchange.getIn().getBody(ProviderPlanSettingsNotificationMessage.class);

        try {
            LOGGER.info("Processing provider plan settings notification for event {} and channel {}", 
                       message.getEventId(), message.getChannelId());

            // Create the webhook DTO with provider plan settings data
            ExternalApiWebhookDto externalApiWebhookDto = new ExternalApiWebhookDto();
            externalApiWebhookDto.setProviderPlanSettings(message.getProviderPlanSettings());
            externalApiWebhookDto.setEventId(message.getEventId());

            // Create headers for the request
            RequestHeaders headers = new RequestHeaders.Builder()
                    .addHeader("ob-action", "PROVIDER_PLAN_SETTINGS_UPDATE")
                    .addHeader("ob-event", "EVENT_CHANNEL")
                    .build();

            // Send notification to api-external
            webhookSendingService.sendNotificationToApiExternal(
                message.getChannelId(), 
                headers, 
                externalApiWebhookDto
            );

            LOGGER.info("Provider plan settings notification sent successfully for event {} and channel {}", 
                       message.getEventId(), message.getChannelId());
        } catch (Exception e) {
            LOGGER.error("Error processing provider plan settings notification for event {} and channel {}", 
                        message.getEventId(), message.getChannelId(), e);
            throw e;
        }
    }
}
