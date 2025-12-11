package es.onebox.event.events.amqp.sessionarchiver;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionArchiverProducerConfiguration {

    @Bean
    public DefaultProducer sessionArchiverProducer(@Value("${amqp.session-archiver.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

    @Bean
    public SessionArchiverProducerService sessionArchiverProducerService() {
        return new SessionArchiverProducerService();
    }
}
