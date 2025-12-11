package es.onebox.event.common.amqp.channelsuggestionscleanup;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChannelSuggestionsCleanUpConfiguration {

    @Bean
    public DefaultProducer suggestionCleanerProducer(@Value("${amqp.channel-suggestions-cleanup.name}") String queueName) {
        return new DefaultProducer(queueName, true);
    }

    @Bean
    public ChannelSuggestionsCleanUpService suggestionCleanerService(DefaultProducer suggestionCleanerProducer) {
        return new ChannelSuggestionsCleanUpService(suggestionCleanerProducer);
    }
}
