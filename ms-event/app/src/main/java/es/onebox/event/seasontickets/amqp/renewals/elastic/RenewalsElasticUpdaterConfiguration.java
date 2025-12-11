package es.onebox.event.seasontickets.amqp.renewals.elastic;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RenewalsElasticUpdaterConfiguration {

    @Bean
    public DefaultProducer renewalsElasticUpdaterProducer(@Value("${amqp.renewal-elastic-updater.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

    @Bean
    public RenewalsElasticUpdaterService renewalsElasticUpdaterService() {
        return new RenewalsElasticUpdaterService();
    }
}
