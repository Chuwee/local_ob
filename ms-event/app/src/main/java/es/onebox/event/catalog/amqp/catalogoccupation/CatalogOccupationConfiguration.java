package es.onebox.event.catalog.amqp.catalogoccupation;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.catalog-occupation")
public class CatalogOccupationConfiguration extends AbstractQueueConfiguration {

}
