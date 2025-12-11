package es.onebox.event.events.amqp.whitelistgeneration;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WhitelistGenerationConfiguration {

    @Value("${amqp.whitelist-generation.name}")
    private String queueName;

    @Bean
    public WhitelistGenerationService whitelistGenerationService() {
        return new WhitelistGenerationService();
    }

    @Bean
    public DefaultProducer whitelistGenerationProducer() {
        return new DefaultProducer(queueName);
    }

}
