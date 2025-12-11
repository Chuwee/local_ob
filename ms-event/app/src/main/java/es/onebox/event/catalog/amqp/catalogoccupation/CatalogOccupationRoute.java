package es.onebox.event.catalog.amqp.catalogoccupation;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatalogOccupationRoute extends RouteBuilder {

    private final CatalogOccupationProcessor catalogOccupationProcessor;

    private final CatalogOccupationConfiguration catalogOccupationConfiguration;

    @Autowired
    public CatalogOccupationRoute(CatalogOccupationProcessor catalogOccupationProcessor, CatalogOccupationConfiguration catalogOccupationConfiguration) {
        this.catalogOccupationProcessor = catalogOccupationProcessor;
        this.catalogOccupationConfiguration = catalogOccupationConfiguration;
    }

    @Override
    public void configure() {
        from(catalogOccupationConfiguration.getRouteURL())
                .id(catalogOccupationConfiguration.getName()).autoStartup(Boolean.FALSE)
                .autoStartup(false)
                .errorHandler(deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI).
                        maximumRedeliveries(3).
                        maximumRedeliveryDelay(30000).
                        useExponentialBackOff().
                        redeliveryDelay(2000).
                        backOffMultiplier(2))
                .process(catalogOccupationProcessor);

    }

}
