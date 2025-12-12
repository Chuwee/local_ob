package es.onebox.ms.notification.externalnotifications.event;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.external-event-notification")
public class ExternalEventConsumeNotificationConfiguration extends AbstractQueueConfiguration {


    @Bean
    public ExternalEventConsumeNotificationRoute externalEventConsumeNotificationRoute() {
        return new ExternalEventConsumeNotificationRoute();
    }

    @Bean
    public ExternalEventConsumeNotificationProcessor externalEventConsumeNotificationProcessor() {
        return new ExternalEventConsumeNotificationProcessor();
    }


}
