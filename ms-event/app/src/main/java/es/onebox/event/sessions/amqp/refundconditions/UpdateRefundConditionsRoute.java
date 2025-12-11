package es.onebox.event.sessions.amqp.refundconditions;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateRefundConditionsRoute extends RouteBuilder {

    private static final String MAX_REDELIVERY_DELAY = "10000";
    private static final String MAX_REDELIVERIES = "5";

    private final UpdateRefundConditionsConfiguration configuration;
    private final UpdateRefundConditionsProcessor processor;

    @Autowired
    public UpdateRefundConditionsRoute(final UpdateRefundConditionsConfiguration configuration,
                                       final UpdateRefundConditionsProcessor processor) {
        this.configuration = configuration;
        this.processor = processor;
    }

    @Override
    public void configure() {
        from(configuration.getRouteURL())
                .id(configuration.getName()).autoStartup(Boolean.FALSE)
                .errorHandler(buildErrorHandler())
                .process(processor);
    }

    private DeadLetterChannelBuilder buildErrorHandler() {
        RedeliveryPolicyDefinition redeliveryPolicy = new RedeliveryPolicyDefinition();
        redeliveryPolicy.setMaximumRedeliveries(MAX_REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MAX_REDELIVERY_DELAY);
        redeliveryPolicy.redeliveryDelay(MAX_REDELIVERY_DELAY);

        DeadLetterChannelBuilder errorHandlerBuilder = new DeadLetterChannelBuilder();
        errorHandlerBuilder.setRedeliveryPolicy(redeliveryPolicy);
        errorHandlerBuilder.setDeadLetterUri(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI);
        return errorHandlerBuilder;
    }
}
