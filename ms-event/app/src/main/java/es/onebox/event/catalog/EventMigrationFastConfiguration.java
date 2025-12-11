package es.onebox.event.catalog;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.event-catalog-es-migration-fast")
public class EventMigrationFastConfiguration extends AbstractQueueConfiguration {

    @Bean
    public EventMigrationRoute eventMigrationFastRoute(EventMigrationProcessor eventMigrationProcessor) {
        return new EventMigrationRoute(this, eventMigrationProcessor, true);
    }

}
