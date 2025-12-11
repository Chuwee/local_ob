package es.onebox.event.products.amqp;

import es.onebox.event.common.amqp.refreshdata.ProductMigrationConfiguration;
import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMigrationRoute extends RouteBuilder {

    private static final String REDELIVERIES = "3";
    private static final String REDELIVERY_DELAY = "2000";
    private static final String MAX_REDELIVERY_DELAY = "30000";
    private static final String BACKOFF_MULTIPLIER = "2";

    @Autowired
    private ProductMigrationConfiguration productMigrationConfiguration;
    @Autowired
    private ProductMigrationProcessor productMigrationProcessor;

    public ProductMigrationRoute(ProductMigrationConfiguration productMigrationConfiguration,
                                 ProductMigrationProcessor productMigrationProcessor) {
        this.productMigrationConfiguration = productMigrationConfiguration;
        this.productMigrationProcessor = productMigrationProcessor;
    }

    @Override
    public void configure() throws Exception {
        from(productMigrationConfiguration.getRouteURL())
                .id(productMigrationConfiguration.getName()).autoStartup(false)
                .delayer(1000L)
                .errorHandler(buildErrorHandler())
                .process(productMigrationProcessor);
    }

    private DeadLetterChannelBuilder  buildErrorHandler() {
        RedeliveryPolicyDefinition redeliveryPolicy = new RedeliveryPolicyDefinition();
        redeliveryPolicy.setMaximumRedeliveries(REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MAX_REDELIVERY_DELAY);
        redeliveryPolicy.redeliveryDelay(REDELIVERY_DELAY);
        redeliveryPolicy.backOffMultiplier(BACKOFF_MULTIPLIER);

        DeadLetterChannelBuilder errorHandlerBuilder = new DeadLetterChannelBuilder();
        errorHandlerBuilder.setRedeliveryPolicy(redeliveryPolicy);
        errorHandlerBuilder.setDeadLetterUri(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI);
        return errorHandlerBuilder;
    }

}
