package es.onebox.event.sessions.amqp.seatgeneration;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenerateSeatConfiguration {

    @Bean
    public DefaultProducer generateSeatProducer(@Value("${amqp.pre-seat-generate.name}") String queueName) {
        return new DefaultProducer(queueName);
    }

    @Bean
    public GenerateSeatService generateSeatService() {
        return new GenerateSeatService();
    }

}
