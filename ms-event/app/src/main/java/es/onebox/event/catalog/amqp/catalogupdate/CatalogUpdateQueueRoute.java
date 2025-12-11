package es.onebox.event.catalog.amqp.catalogupdate;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatalogUpdateQueueRoute extends RouteBuilder {

    private final CatalogUpdateQueueProcessor catalogUpdateQueueProcessor;

    private final CatalogUpdateQueueConfiguration catalogUpdateQueueConfiguration;

    @Autowired
    public CatalogUpdateQueueRoute(CatalogUpdateQueueProcessor catalogUpdateQueueProcessor, CatalogUpdateQueueConfiguration catalogUpdateQueueConfiguration) {
        this.catalogUpdateQueueProcessor = catalogUpdateQueueProcessor;
        this.catalogUpdateQueueConfiguration = catalogUpdateQueueConfiguration;
    }

    @Override
    public void configure() {
        from(catalogUpdateQueueConfiguration.getRouteURL())
                .id(catalogUpdateQueueConfiguration.getName()).autoStartup(Boolean.FALSE)
                .autoStartup(false)
                .errorHandler(deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI).
                        maximumRedeliveries(3).
                        maximumRedeliveryDelay(30000).
                        useExponentialBackOff().
                        redeliveryDelay(2000).
                        backOffMultiplier(2))
                .process(catalogUpdateQueueProcessor);

    }

}
