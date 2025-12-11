package es.onebox.event.sessions.amqp.sessionclone;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionCloneProducerConfiguration {

    @Bean
    public DefaultProducer sessionCloneProducer(@Value("${amqp.session-clone.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

    @Bean
    public SessionCloneService sessionCloneService() {
        return new SessionCloneService();
    }

}
