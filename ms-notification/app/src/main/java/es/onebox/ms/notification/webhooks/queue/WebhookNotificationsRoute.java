package es.onebox.ms.notification.webhooks.queue;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebhookNotificationsRoute extends RouteBuilder {

    private final WebhookNotificationsConfiguration webhookNotificationsQueueConfiguration;
    private final WebhookNotificationsRoutingProcessor webhookNotificationsRoutingProcessor;

    @Autowired
    public WebhookNotificationsRoute(WebhookNotificationsConfiguration webhookNotificationsQueueConfiguration,
                                     WebhookNotificationsRoutingProcessor webhookNotificationsRoutingProcessor) {
        this.webhookNotificationsQueueConfiguration = webhookNotificationsQueueConfiguration;
        this.webhookNotificationsRoutingProcessor = webhookNotificationsRoutingProcessor;
    }

    @Override
    public void configure() {

        from(webhookNotificationsQueueConfiguration.getRouteURL())
                .id(webhookNotificationsQueueConfiguration.getName())
                .autoStartup(Boolean.FALSE)
                .errorHandler(deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI)
                        .maximumRedeliveries(3)
                        .maximumRedeliveryDelay(30000)
                        .useExponentialBackOff()
                        .redeliveryDelay(2000)
                        .backOffMultiplier(2))
                .process(webhookNotificationsRoutingProcessor);
    }
}
