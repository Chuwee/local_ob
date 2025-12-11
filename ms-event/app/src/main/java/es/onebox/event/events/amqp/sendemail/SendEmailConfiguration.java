package es.onebox.event.events.amqp.sendemail;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendEmailConfiguration {

    @Bean
    @Qualifier("sendEmailProducer")
    public DefaultProducer sendEmailProducer(@Value("${amqp.send-email.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

}
