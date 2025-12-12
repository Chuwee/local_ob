package es.onebox.internal.automaticrenewals.eip.process;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.automatic-renewals")
public class AutomaticRenewalsConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer automaticRenewalsProducer() {
        return new DefaultProducer(getName(), true);
    }

}
