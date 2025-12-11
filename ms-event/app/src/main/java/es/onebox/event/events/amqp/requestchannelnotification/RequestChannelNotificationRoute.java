package es.onebox.event.events.amqp.requestchannelnotification;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestChannelNotificationRoute extends RouteBuilder {

    private static final String REDELIVERIES = "5";
    private static final String REDELIVERY_DELAY = "10000";
    private static final String MAX_REDELIVERY_DELAY = "10000";

    @Autowired
    private RequestChannelNotificationProcessor requestChannelNotificationProcessor;
    @Autowired
    private RequestChannelNotificationConfiguration requestChannelNotificationConfiguration;

    @Override
    public void configure() {
        from(requestChannelNotificationConfiguration.getRouteURL())
                .id(requestChannelNotificationConfiguration.getName()).autoStartup(Boolean.FALSE)
                .autoStartup(false)
                .errorHandler(buildErrorHandler())
                .process(requestChannelNotificationProcessor);
    }

    private DeadLetterChannelBuilder buildErrorHandler() {
        RedeliveryPolicyDefinition redeliveryPolicy = new RedeliveryPolicyDefinition();
        redeliveryPolicy.setMaximumRedeliveries(REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MAX_REDELIVERY_DELAY);
        redeliveryPolicy.redeliveryDelay(REDELIVERY_DELAY);

        DeadLetterChannelBuilder errorHandlerBuilder = new DeadLetterChannelBuilder();
        errorHandlerBuilder.setRedeliveryPolicy(redeliveryPolicy);
        errorHandlerBuilder.setDeadLetterUri(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI);
        return errorHandlerBuilder;
    }


}
