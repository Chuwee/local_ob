package es.onebox.event.events.amqp.tiermodification;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.tier-modification")
public class TierModificationConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer tierModificationProducer() {
        return new DefaultProducer(getName());
    }

}
