package es.onebox.event.events.amqp.providerplansettingsnotification;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderPlanSettingsNotificationConfiguration {

    @Value("${amqp.provider-plan-settings-notification.name:provider-plan-settings-notification}")
    private String queueName;

    @Bean
    public DefaultProducer providerPlanSettingsNotificationProducer() {
        return new DefaultProducer(queueName);
    }
}
