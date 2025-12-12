package es.onebox.ms.notification.externalnotifications.event;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class ExternalEventConsumeNotificationRoute extends RouteBuilder {

    @Autowired
    private ExternalEventConsumeNotificationProcessor externalEventConsumeNotificationProcessor;

    @Autowired
    private ExternalEventConsumeNotificationConfiguration externalEventConsumeNotificationConfiguration;

    @Override
    public void configure() {
        from(externalEventConsumeNotificationConfiguration.getRouteURL())
                .autoStartup(Boolean.FALSE)
                .id(externalEventConsumeNotificationConfiguration.getName())
                .process(externalEventConsumeNotificationProcessor);
    }


}
