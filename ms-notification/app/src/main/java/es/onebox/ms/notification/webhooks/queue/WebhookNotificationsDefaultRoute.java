package es.onebox.ms.notification.webhooks.queue;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebhookNotificationsDefaultRoute extends RouteBuilder {

    private final WebhookNotificationsProcessor webhookNotificationsQueueProcessor;
    private final WebhookNotificationsDefaultConfiguration webhookDefaultNotificationsQueueConfiguration;

    @Autowired
    public WebhookNotificationsDefaultRoute(WebhookNotificationsProcessor webhookNotificationsQueueProcessor,
                                            WebhookNotificationsDefaultConfiguration webhookDefaultNotificationsQueueConfiguration) {
        this.webhookNotificationsQueueProcessor = webhookNotificationsQueueProcessor;
        this.webhookDefaultNotificationsQueueConfiguration = webhookDefaultNotificationsQueueConfiguration;
    }

    @Override
    public void configure() {
        from(webhookDefaultNotificationsQueueConfiguration.getRouteURL())
                .id(webhookDefaultNotificationsQueueConfiguration.getName())
                .errorHandler(deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI)
                        .maximumRedeliveries(3)
                        .maximumRedeliveryDelay(30000)
                        .useExponentialBackOff()
                        .redeliveryDelay(2000)
                        .backOffMultiplier(2))
                .process(webhookNotificationsQueueProcessor);
    }

}
