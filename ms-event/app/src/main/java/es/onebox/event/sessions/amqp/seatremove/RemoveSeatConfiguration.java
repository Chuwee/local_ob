package es.onebox.event.sessions.amqp.seatremove;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoveSeatConfiguration {

    @Value("${amqp.seat-remove.name}")
    private String queueName;

    @Bean
    public DefaultProducer seatRemoveProducer() {
        return new DefaultProducer(queueName);
    }

    @Bean
    public SeatRemoveService seatRemoveService() {
        return new SeatRemoveService();
    }

}
