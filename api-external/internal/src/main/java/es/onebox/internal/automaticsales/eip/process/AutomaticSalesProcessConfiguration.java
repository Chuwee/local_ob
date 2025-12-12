package es.onebox.internal.automaticsales.eip.process;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.automatic-sales")
public class AutomaticSalesProcessConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer automaticSalesProcessProducer() {
        return new DefaultProducer(getName(), true);
    }

}
