package es.onebox.event.events.amqp.avetintegration;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationAvetConfiguration {

    @Value("${amqp.avet-integration.name}")
    private String avetIntegrationQueue;

    @Bean
    public DefaultProducer integrationAvetProducer() {
        return new DefaultProducer(avetIntegrationQueue);
    }

    @Bean
    public IntegrationAvetService integrationAvetService() {
        return new IntegrationAvetService(this.integrationAvetProducer());
    }

}
