package es.onebox.event.catalog.amqp.catalogpacksupdate;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.catalog-packs-update")
public class CatalogPacksUpdateConfiguration extends AbstractQueueConfiguration {

    public static final String REFRESH_PACK_RELATED_EVENTS_HEADER = "REFRESH_PACK_RELATED_EVENTS";
    public static final String REFRESH_EVENT_RELATED_PACKS_HEADER = "REFRESH_EVENT_RELATED_PACKS";

    @Bean
    public CatalogPacksUpdateRoute packsUpdateRoute() {
        return new CatalogPacksUpdateRoute();
    }

    @Bean
    public CatalogPacksUpdateProcessor packsUpdateConsumer() {
        return new CatalogPacksUpdateProcessor();
    }
}
