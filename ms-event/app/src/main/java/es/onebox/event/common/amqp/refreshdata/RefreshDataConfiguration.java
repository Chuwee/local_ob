package es.onebox.event.common.amqp.refreshdata;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshDataConfiguration {

    @Value("${amqp.refresh-data.name}")
    private String queueName;

    @Bean
    public DefaultProducer refreshDataProducer() {
        return new DefaultProducer(queueName);
    }
    
}
