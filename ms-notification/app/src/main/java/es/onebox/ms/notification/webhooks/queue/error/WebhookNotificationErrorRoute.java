package es.onebox.ms.notification.webhooks.queue.error;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class WebhookNotificationErrorRoute extends RouteBuilder {

    @Autowired
    private WebhookNotificationErrorProcessor webhookNotificationErrorProcessor;
    @Autowired
    private WebhookNotificationErrorConfiguration webhookNotificationErrorConfiguration;

    @Override
    public void configure() {
        from(webhookNotificationErrorConfiguration.getRouteURL())
                .id(webhookNotificationErrorConfiguration.getName())
                .autoStartup(Boolean.FALSE)
                .errorHandler(deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI))
                .process(webhookNotificationErrorProcessor);
    }

}
