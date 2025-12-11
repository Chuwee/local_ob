package es.onebox.event.seasontickets.amqp.renewals.purge;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.renewal-purge-seats")
public class PurgeRenewalSeatsConfiguration extends AbstractQueueConfiguration {

    @Value("${amqp.renewal-purge-seats.name}")
    private String queueName;

    @Bean
    public DefaultProducer renewalSeatsPurgeProducer() {
        return new DefaultProducer(queueName);
    }

    @Bean
    public PurgeRenewalSeatsProducerService renewalSeatsProducerService() {
        return new PurgeRenewalSeatsProducerService();
    }
}
