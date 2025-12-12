package es.onebox.atm.wallet.eip;

import es.onebox.atm.config.ATMExternalWalletQueueConfig;
import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ATMExternalWalletRoute extends RouteBuilder {

    public static final String MAX_REDELIVERS = "5";

    @Autowired
    private ATMExternalWalletQueueConfig atmExternalWalletQueueConfig;

    @Autowired
    private ATMExternalWalletProcessor atmExternalWalletProcessor;

    @Override
    public void configure() {
        from(atmExternalWalletQueueConfig.getRouteURL())
                .id(atmExternalWalletQueueConfig.getName())
                .errorHandler(externalWalletErrorHandlerBuilder())
                .process(atmExternalWalletProcessor);
    }

    private DeadLetterChannelBuilder externalWalletErrorHandlerBuilder() {
        RedeliveryPolicyDefinition redeliveryPolicy = new RedeliveryPolicyDefinition();
        redeliveryPolicy.setMaximumRedeliveries(MAX_REDELIVERS);
        redeliveryPolicy.setMaximumRedeliveryDelay("240000");
        redeliveryPolicy.useExponentialBackOff();
        redeliveryPolicy.redeliveryDelay(30000);
        redeliveryPolicy.backOffMultiplier(2);

        DeadLetterChannelBuilder errorHandlerBuilder = new DeadLetterChannelBuilder();
        errorHandlerBuilder.setRedeliveryPolicy(redeliveryPolicy);
        errorHandlerBuilder.setDeadLetterUri(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI);
        return errorHandlerBuilder;
    }
}
