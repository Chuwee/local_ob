package es.onebox.ms.notification.webhooks.queue;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.webhook-notification-order")
public class WebhookNotificationsOrderConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer webhookNotificationOrderProducer() {
        return new DefaultProducer(getName(), true);
    }
}
