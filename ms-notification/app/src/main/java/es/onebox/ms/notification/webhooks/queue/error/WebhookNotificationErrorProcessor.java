package es.onebox.ms.notification.webhooks.queue.error;

import es.onebox.message.broker.client.message.DefaultNotificationMessage;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static es.onebox.ms.notification.webhooks.queue.WebhookNotificationsConfiguration.HEADER_REDELIVERY_COUNTER;

public class WebhookNotificationErrorProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookNotificationErrorProcessor.class);

    public static final String WEBHOOK_ERROR_RETRIES = "WEBHOOK_ERROR_RETRIES";

    private static final int RETRY_LIMIT = 10;

    @Autowired
    private DefaultProducer webhookNotificationProducer;

    @Override
    public void execute(Exchange exchange) throws Exception {
        DefaultNotificationMessage message = exchange.getIn().getBody(DefaultNotificationMessage.class);

        Map<String, Object> headers = exchange.getIn().getHeaders();
        Integer retry = (Integer) headers.getOrDefault(WEBHOOK_ERROR_RETRIES, 0);
        if (retry > RETRY_LIMIT) {
            LOGGER.error("[WEBHOOK] code: {} - Re-enqueue limit exceed after {} retries - Webhook not delivered!",
                    message.getMessage(), RETRY_LIMIT);
            return;
        }

        LOGGER.info("[WEBHOOK] Enqueue code: {} from error queue to retry webhook delivery with retry #{}",
                message.getMessage(), retry);
        headers.remove(HEADER_REDELIVERY_COUNTER);
        webhookNotificationProducer.sendMessage(message, headers);
    }

}
