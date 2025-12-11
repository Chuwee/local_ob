package es.onebox.event.common.amqp.webhook;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebhookNotificationConfiguration {

    @Bean
    public DefaultProducer webhookNotificationProducer(@Value("${amqp.webhook-notification.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

    @Bean
    public WebhookService webhookService(DefaultProducer webhookNotificationProducer) {
        return new WebhookService(webhookNotificationProducer);
    }

}
