package es.onebox.event.seasontickets.amqp.renewals.revert;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RevertRenewalRoute extends RouteBuilder {

    @Autowired
    private RevertRenewalConfiguration config;

    @Autowired
    private RevertRenewalProcessor processor;

    @Override
    public void configure() {
        from(config.getRouteURL())
                .id(config.getName())
                .autoStartup(false)
                .process(processor);
    }
}
