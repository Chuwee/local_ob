package es.onebox.event.events.amqp.eventremove;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventRemoveProducerConfiguration {

    @Value("${amqp.event-remove.name}")
    private String queueName;

    @Bean
    public DefaultProducer eventRemoveProducer() {
        return new DefaultProducer(queueName);
    }

    @Bean
    public EventRemoveService eventRemoveService() {
        return new EventRemoveService();
    }

}
