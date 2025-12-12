package es.onebox.ms.notification.webhooks.queue.error;

import org.apache.camel.spring.SpringCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

public class WebhookNotificationsScheduledRetry {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookNotificationsScheduledRetry.class);

    private static final String CRON_EXPRESSION = "0 0/30 * * * *"; //Each 30 minutes

    @Autowired
    private ApplicationContext context;
    @Autowired
    private WebhookNotificationErrorConfiguration webhookNotificationErrorConfiguration;

    @Scheduled(cron = CRON_EXPRESSION)
    public void retryWebhookNotification() throws Exception {
        LOGGER.info("[WEBHOOK RETRY] Start scheduled error handling to re-enqueue webhook notifications");

        SpringCamelContext camel = (SpringCamelContext) context.getBean("camelContext");
        camel.startRoute(webhookNotificationErrorConfiguration.getName());

        Thread.sleep(5000);

        camel.suspendRoute(webhookNotificationErrorConfiguration.getName());

        LOGGER.info("[WEBHOOK RETRY] Stop scheduled retry error queue");
    }
}
