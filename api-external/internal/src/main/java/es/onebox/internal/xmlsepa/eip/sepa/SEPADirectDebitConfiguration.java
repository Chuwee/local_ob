package es.onebox.internal.xmlsepa.eip.sepa;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.xml-sepa-generation")
public class SEPADirectDebitConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer sepaGenerationProducer() {
        return new DefaultProducer(getName(), true);
    }

}
