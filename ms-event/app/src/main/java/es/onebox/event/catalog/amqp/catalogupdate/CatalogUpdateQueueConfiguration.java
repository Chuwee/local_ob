package es.onebox.event.catalog.amqp.catalogupdate;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.catalog-update")
public class CatalogUpdateQueueConfiguration extends AbstractQueueConfiguration {

}
