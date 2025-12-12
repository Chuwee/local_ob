package es.onebox.internal.automaticsales.eip.process;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutomaticSalesProcessRoute extends RouteBuilder {
    private final AutomaticSalesProcessConsumer consumer;
    private final AutomaticSalesProcessConfiguration configuration;
    private static final String REDELIVERY_TIME = "0";
    private static final String MAX_REDELIVERIES = "0";

    @Autowired
    public AutomaticSalesProcessRoute(AutomaticSalesProcessConsumer consumer, AutomaticSalesProcessConfiguration configuration
    ) {
        this.consumer = consumer;
        this.configuration = configuration;
    }

    @Override
    public void configure() {
        from(configuration.getRouteURL())
                .id(configuration.getName())
                .autoStartup(Boolean.TRUE)
                .errorHandler(externalInvoiceErrorHandlerBuilder())
                .process(consumer);
    }

    private DeadLetterChannelBuilder externalInvoiceErrorHandlerBuilder() {
        DeadLetterChannelBuilder errorHandlerBuilder = new DeadLetterChannelBuilder();
        errorHandlerBuilder.setDeadLetterUri(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI);
        errorHandlerBuilder.setRedeliveryPolicy(redeliveryPolicy());
        return errorHandlerBuilder;
    }

    private RedeliveryPolicyDefinition redeliveryPolicy() {
        RedeliveryPolicyDefinition redeliveryPolicy = new RedeliveryPolicyDefinition();
        redeliveryPolicy.setRedeliveryDelay(REDELIVERY_TIME);
        redeliveryPolicy.setMaximumRedeliveries(MAX_REDELIVERIES);
        return redeliveryPolicy;
    }
}
