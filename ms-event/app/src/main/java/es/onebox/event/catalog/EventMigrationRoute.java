package es.onebox.event.catalog;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.errorhandler.DefaultErrorHandlerDefinition;

public class EventMigrationRoute extends RouteBuilder {

    private static final String HEADER_FAST_MIGRATION = "fastMigration";

    private final AbstractQueueConfiguration eventMigrationConfiguration;
    private final EventMigrationProcessor eventMigrationProcessor;
    private final boolean fastMigration;

    public EventMigrationRoute(AbstractQueueConfiguration eventMigrationConfiguration,
                               EventMigrationProcessor eventMigrationProcessor,
                               boolean fastMigration) {
        this.eventMigrationConfiguration = eventMigrationConfiguration;
        this.eventMigrationProcessor = eventMigrationProcessor;
        this.fastMigration = fastMigration;
    }

    @Override
    public void configure() {
        from(eventMigrationConfiguration.getRouteURL())
                .id(eventMigrationConfiguration.getName())
                .autoStartup(Boolean.FALSE)
                .errorHandler(getErrorHandlerBuilder())
                .choice()
                .when(PredicateBuilder.or(
                        PredicateBuilder.and(
                                constant(fastMigration).isEqualTo(Boolean.TRUE),
                                header(HEADER_FAST_MIGRATION).isEqualTo(Boolean.TRUE)),
                        PredicateBuilder.and(
                                constant(fastMigration).isEqualTo(Boolean.FALSE),
                                header(HEADER_FAST_MIGRATION).isNotEqualTo(Boolean.TRUE))
                ))
                .process(eventMigrationProcessor);

    }

    private DefaultErrorHandlerDefinition getErrorHandlerBuilder() {
        return deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI).
                maximumRedeliveries(3).
                maximumRedeliveryDelay(30000).
                useExponentialBackOff().
                redeliveryDelay(2000).
                backOffMultiplier(2);
    }

}
