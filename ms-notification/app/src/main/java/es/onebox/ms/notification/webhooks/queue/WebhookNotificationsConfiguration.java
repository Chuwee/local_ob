package es.onebox.ms.notification.webhooks.queue;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.webhook-notification")
public class WebhookNotificationsConfiguration extends AbstractQueueConfiguration {

    public static final String HEADER_REDELIVERY_COUNTER = "CamelRedeliveryCounter";
    public static final String HEADER_REDELIVERY_LIMIT = "CamelRedeliveryMaxCounter";

    @Bean
    public DefaultProducer webhookNotificationProducer() {
        return new DefaultProducer(getName(), true);
    }
}
