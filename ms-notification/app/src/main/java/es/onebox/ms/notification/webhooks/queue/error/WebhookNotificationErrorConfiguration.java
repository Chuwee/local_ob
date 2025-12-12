package es.onebox.ms.notification.webhooks.queue.error;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.webhook-notification-error")
public class WebhookNotificationErrorConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer webhookNotificationErrorProducer() {
        return new DefaultProducer(getName(), true);
    }

    @Bean
    public WebhookNotificationsScheduledRetry webhookNotificationsScheduledRetry() {
        return new WebhookNotificationsScheduledRetry();
    }

    @Bean
    public WebhookNotificationErrorRoute webhookNotificationErrorRoute() {
        return new WebhookNotificationErrorRoute();
    }

    @Bean
    public WebhookNotificationErrorProcessor webhookNotificationErrorProcessor() {
        return new WebhookNotificationErrorProcessor();
    }

}
