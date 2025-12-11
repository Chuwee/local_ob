package es.onebox.event.events.amqp.tiermodification;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TierModificationRoute extends RouteBuilder {

    private static final String REDELIVERIES = "5";
    private static final String REDELIVERY_DELAY = "10000";
    private static final String MAX_REDELIVERY_DELAY = "10000";
    private static final String AGGREGATION_INTERVAL = "3000";

    @Autowired
    private TierModificationProcessor tierModificationProcessor;
    @Autowired
    private TierModificationConfiguration tierModificationConfiguration;

    @Override
    public void configure() {
        from(tierModificationConfiguration.getRouteURL())
                .id(tierModificationConfiguration.getName())
                .autoStartup(Boolean.FALSE)
                .errorHandler(buildErrorHandler())
                .choice()
                .when(bodyAs(String.class).contains("EVALUATE_TIERS"))
                .aggregate(bodyAs(String.class), new TierEvaluationAggregationStrategy())
                .completionInterval(AGGREGATION_INTERVAL)
                .process(tierModificationProcessor)
                .endChoice()
                .otherwise()
                .process(tierModificationProcessor);
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

    public static class TierEvaluationAggregationStrategy implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }
            return oldExchange;
        }
    }

}
