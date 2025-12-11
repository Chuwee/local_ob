package es.onebox.mgmt.seasontickets.amqp.createsession;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateSeasonTicketsSessionConfiguration {

    @Bean
    public DefaultProducer createSeasonTicketSessionProducer(@Value("${amqp.create-season-ticket-session.name}") String queueName) {
        return new DefaultProducer(queueName);
    }

}
