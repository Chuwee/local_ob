package es.onebox.fever.config;

import es.onebox.message.broker.kafka.DefaultKafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("es.onebox.fever")
public class FeverConfig {

    public static final String WEBHOOK_NAME = "event.onebox.webhook.on_webhook_received";
    public static final String WEBHOOK_TOPIC = "json." + WEBHOOK_NAME;

    @Bean
    public DefaultKafkaProducer feverWebhookProducer() {
        return new DefaultKafkaProducer(WEBHOOK_TOPIC);
    }

}
