package es.onebox.event.seasontickets.amqp.renewals.cancel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelRenewalsRoute extends RouteBuilder {

    @Autowired
    private CancelRenewalsConfiguration config;

    @Autowired
    private CancelRenewalsProcessor processor;

    @Override
    public void configure() {
        from(config.getRouteURL())
                .id(config.getName())
                .autoStartup(false)
                .process(processor);
    }
}
