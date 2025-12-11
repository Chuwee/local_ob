package es.onebox.event.common.amqp.refreshdata;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.product-catalog-cb-migration")
public class ProductMigrationConfiguration extends AbstractQueueConfiguration {

    @Value("${amqp.product-catalog-cb-migration.name}")
    private String queueName;

    @Bean
    public DefaultProducer productMigrationProducer() {
        return new DefaultProducer(queueName);
    }

}
