package es.onebox.event.seasontickets.amqp.renewals.refund;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefundRenewalsRoute extends RouteBuilder {

    @Autowired
    private RefundRenewalsConfiguration config;

    @Autowired
    private RefundRenewalsProcessor processor;

    @Override
    public void configure() {
        from(config.getRouteURL())
                .id(config.getName())
                .autoStartup(false)
                .process(processor);
    }
}
