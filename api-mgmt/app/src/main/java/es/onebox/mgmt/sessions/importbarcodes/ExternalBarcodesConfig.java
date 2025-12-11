package es.onebox.mgmt.sessions.importbarcodes;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalBarcodesConfig {

    @Bean
    public DefaultProducer externalBarcodesProducer(@Value("${amqp.external-barcodes.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }
}
