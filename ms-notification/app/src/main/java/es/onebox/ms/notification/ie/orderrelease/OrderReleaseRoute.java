package es.onebox.ms.notification.ie.orderrelease;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderReleaseRoute extends RouteBuilder {

    @Autowired
    private OrderReleaseConfiguration orderReleaseConfiguration;

    @Autowired
    private OrderReleaseProcessor orderReleaseProcessor;

    @Override
    public void configure() {
        from(orderReleaseConfiguration.getRouteURL())
                .autoStartup(Boolean.FALSE)
                .id(orderReleaseConfiguration.getName())
                .process(orderReleaseProcessor);
    }

}
