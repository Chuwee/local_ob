package es.onebox.event.events.amqp.eventnotification;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalEventNotificationConfiguration {

    @Bean
    public DefaultProducer externalEventConsumeNotificationProducer(@Value("${amqp.external-event-notification.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

}
