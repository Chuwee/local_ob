package es.onebox.event.seasontickets.amqp.renewals.relatedseats;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RenewalsUpdateRelatedSeatsConfiguration {

    @Bean
    public DefaultProducer renewalsUpdateRelatedSeatsProducer(@Value("${amqp.renewal-update-related-seats.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

    @Bean
    public RenewalsUpdateRelatedSeatsService renewalsUpdateRelatedSeatsService() {
        return new RenewalsUpdateRelatedSeatsService();
    }
}
