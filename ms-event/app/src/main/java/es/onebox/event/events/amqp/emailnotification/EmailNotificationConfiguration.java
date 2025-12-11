package es.onebox.event.events.amqp.emailnotification;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailNotificationConfiguration {

    @Value("${amqp.email-notification.name}")
    private String queueName;

    @Bean
    public EmailNotificationService emailNotificationService() {
        return new EmailNotificationService();
    }

    @Bean
    public DefaultProducer emailNotificationProducer() {
        return new DefaultProducer(queueName);
    }

}
