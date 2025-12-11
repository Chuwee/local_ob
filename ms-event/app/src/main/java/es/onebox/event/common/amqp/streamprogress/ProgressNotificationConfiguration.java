package es.onebox.event.common.amqp.streamprogress;

import es.onebox.message.broker.producer.exchange.DefaultTopicProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProgressNotificationConfiguration {

    @Value("${amqp.stream-progress.name}")
    private String exchangeProducerName;

    @Bean
    public DefaultTopicProducer notificationProgressProducer() {
        return new DefaultTopicProducer(exchangeProducerName);
    }

    @Bean
    public ProgressService progressService() {
        return new ProgressService();
    }

}

