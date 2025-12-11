package es.onebox.mgmt.channels.members.eip;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MembersBatchPricesQueueConfig {

    @Bean
    public DefaultProducer batchPricesProducer(@Value("${amqp.member-batch-prices.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

}
