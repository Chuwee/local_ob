package es.onebox.event.common.amqp.webhook;

import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookService.class);

    private static final String WEBHOOK_EVENT = "EVENT";
    private static final String WEBHOOK_SESSION = "SESSION";
    private static final String WEBHOOK_PRODUCT = "PRODUCT";

    private final DefaultProducer webhookNotificationProducer;

    @Autowired
    public WebhookService(DefaultProducer webhookNotificationProducer) {
        this.webhookNotificationProducer = webhookNotificationProducer;
    }

    public void sendEventNotification(Long id) {
        sendWebhook(id, null, WEBHOOK_EVENT, null);
    }

    public void sendEventNotification(Long id, NotificationSubtype notificationSubtype) {
        sendWebhook(id, null, WEBHOOK_EVENT, notificationSubtype);
    }

    public void sendSessionNotification(Long id, NotificationSubtype notificationSubtype) {
        sendWebhook(id,null, WEBHOOK_SESSION, notificationSubtype);
    }

    public void sendSessionNotification(Long id, Long channelId, NotificationSubtype notificationSubtype) {
        sendWebhook(id, channelId ,WEBHOOK_SESSION, notificationSubtype);
    }

    public void sendWebhookSessionDelete(Long id, Long eventId, NotificationSubtype notificationSubtype) {
        try {
            webhookNotificationProducer.sendMessage(new WebhookSessionDeleteNotificationMessage(id, eventId, WEBHOOK_SESSION, notificationSubtype));
        } catch (Exception e) {
            LOGGER.error("Error while sending webhook for event", e);
        }
    }

    public void sendWebhookGenericEvent(Long id, Long templateId, Long rateId, Long channelId, NotificationSubtype notificationSubtype) {
        try {
            webhookNotificationProducer.sendMessage(new WebhookEventGenericNotificationMessage(
                id, WEBHOOK_EVENT, channelId, templateId, rateId, notificationSubtype
            ));
        } catch (Exception e) {
            LOGGER.error("Error while sending webhook for event", e);
        }
    }

    public void sendProductNotification(Long id, NotificationSubtype notificationSubtype) {
        sendWebhook(id,null, WEBHOOK_PRODUCT, notificationSubtype);
    }

    public void sendProductChannelDeleteNotification(Long id, Long channelId, NotificationSubtype notificationSubtype) {
        sendWebhook(id, channelId, WEBHOOK_PRODUCT, notificationSubtype);
    }

    public void sendProductChannelNotification(Long id, Long channelId, NotificationSubtype notificationSubtype) {
        sendWebhook(id,channelId, WEBHOOK_PRODUCT, notificationSubtype);
    }

    public void sendProductSessionNotification(Long id, Long eventId, NotificationSubtype notificationSubtype) {
        try {
            webhookNotificationProducer.sendMessage(new WebhookProductSessionNotificationMessage(id, eventId, WEBHOOK_PRODUCT, notificationSubtype));
        } catch (Exception e) {
            LOGGER.error("Error while sending webhook for product session", e);
        }
    }

    private void sendWebhook(Long id,  Long channelId, String event, NotificationSubtype notificationSubtype) {
        try {
            webhookNotificationProducer.sendMessage(new WebhookEventNotificationMessage(id, channelId, event, notificationSubtype));
        } catch (Exception e) {
            LOGGER.error("Error while sending webhook for event", e);
        }
    }
}
