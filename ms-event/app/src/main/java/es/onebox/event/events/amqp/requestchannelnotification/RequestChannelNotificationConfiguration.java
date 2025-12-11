package es.onebox.event.events.amqp.requestchannelnotification;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.request-channel-notification")
public class RequestChannelNotificationConfiguration extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer requestChannelNotificationProducer() {
        return new DefaultProducer(getName());
    }

}
