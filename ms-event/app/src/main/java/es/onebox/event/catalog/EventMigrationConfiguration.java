package es.onebox.event.catalog;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.service.Event2ESService;
import es.onebox.event.catalog.service.PackRefreshService;
import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.event-catalog-es-migration")
public class EventMigrationConfiguration extends AbstractQueueConfiguration {

    public static final String HEADER_REFRESH_ONLY_AVAILABILITY = "REFRESH_ONLY_AVAILABILITY";

    @Bean
    public EventMigrationRoute eventMigrationRoute(EventMigrationProcessor eventMigrationProcessor) {
        return new EventMigrationRoute(this, eventMigrationProcessor, false);
    }

    @Bean
    public EventMigrationProcessor eventMigrationProcessor(Event2ESService event2ESService, CacheRepository cacheRepository, PackRefreshService packRefreshService) {
        return new EventMigrationProcessor(event2ESService, cacheRepository, packRefreshService);
    }


}
