package es.onebox.ms.notification.webhooks.queue;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebhookNotificationsOrderRoute extends RouteBuilder {

    private final WebhookNotificationsProcessor webhookNotificationsQueueProcessor;

    private final WebhookNotificationsOrderConfiguration webhookNotificationsOrderConfiguration;

    @Autowired
    public WebhookNotificationsOrderRoute(WebhookNotificationsProcessor webhookNotificationsQueueProcessor,
                                          WebhookNotificationsOrderConfiguration webhookNotificationsOrderConfiguration) {
        this.webhookNotificationsQueueProcessor = webhookNotificationsQueueProcessor;
        this.webhookNotificationsOrderConfiguration = webhookNotificationsOrderConfiguration;
    }

    @Override
    public void configure() {
        from(webhookNotificationsOrderConfiguration.getRouteURL())
                .id(webhookNotificationsOrderConfiguration.getName())
                .errorHandler(deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI)
                        .maximumRedeliveries(3)
                        .maximumRedeliveryDelay(30000)
                        .useExponentialBackOff()
                        .redeliveryDelay(2000)
                        .backOffMultiplier(2))
                .delayer(5000L) //5 seconds delay to send an order
                .process(webhookNotificationsQueueProcessor);
    }

}
