package es.onebox.event.sessions.amqp.refundconditions;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.session-packs-refund-conditions")
public class UpdateRefundConditionsConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer refundConditionsProducer() {
        return new DefaultProducer(getName());
    }

}
