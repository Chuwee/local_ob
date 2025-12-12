package es.onebox.atm.barcode.eip;

import es.onebox.atm.config.ATMExternalBarcodeQueueConfig;
import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ATMExternalBarcodeRoute extends RouteBuilder {

    public static final String MAX_REDELIVERS = "5";

    @Autowired
    private ATMExternalBarcodeQueueConfig atmExternalBarcodeQueueConfig;

    @Autowired
    private ATMExternalBarcodeProcessor atmExternalBarcodeProcessor;

    @Override
    public void configure() {
        from(atmExternalBarcodeQueueConfig.getRouteURL())
                .id(atmExternalBarcodeQueueConfig.getName())
                .errorHandler(externalBarcodeErrorHandlerBuilder())
                .process(atmExternalBarcodeProcessor);
    }

    private DeadLetterChannelBuilder externalBarcodeErrorHandlerBuilder() {
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
