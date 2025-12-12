package es.onebox.atm.webhook.eip;

import es.onebox.atm.config.ATMSalesforcePushNotificationQueueConfig;
import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ATMSalesforcePushNotificationRoute extends RouteBuilder {

    public static final String MAX_REDELIVERS = "5";

    @Autowired
    private ATMSalesforcePushNotificationQueueConfig atmSalesforcePushNotificationQueueConfig;

    @Autowired
    private ATMSalesforcePushNotificationProcessor atmSalesforcePushNotificationProcessor;

    @Override
    public void configure() {
        from(atmSalesforcePushNotificationQueueConfig.getRouteURL())
                .id(atmSalesforcePushNotificationQueueConfig.getName())
                .errorHandler(salesforcePushNotificationErrorHandlerBuilder())
                .process(atmSalesforcePushNotificationProcessor);
    }

    private DeadLetterChannelBuilder salesforcePushNotificationErrorHandlerBuilder() {
        RedeliveryPolicyDefinition redeliveryPolicy = new RedeliveryPolicyDefinition();
        redeliveryPolicy.setMaximumRedeliveries(MAX_REDELIVERS);
        redeliveryPolicy.setMaximumRedeliveryDelay("90000");
        redeliveryPolicy.useExponentialBackOff();
        redeliveryPolicy.redeliveryDelay(3000);
        redeliveryPolicy.backOffMultiplier(2);
        DeadLetterChannelBuilder errorHandlerBuilder = new DeadLetterChannelBuilder();
        errorHandlerBuilder.setRedeliveryPolicy(redeliveryPolicy);
        errorHandlerBuilder.setDeadLetterUri(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI);
        return errorHandlerBuilder;
    }
}
