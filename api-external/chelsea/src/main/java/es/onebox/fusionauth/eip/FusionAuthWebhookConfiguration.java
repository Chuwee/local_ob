package es.onebox.fusionauth.eip;


import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FusionAuthWebhookConfiguration {

    @Bean
    public DefaultProducer fusionAuthWebhookProducer(@Value("${amqp.fusion-auth-webhooks.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

}
