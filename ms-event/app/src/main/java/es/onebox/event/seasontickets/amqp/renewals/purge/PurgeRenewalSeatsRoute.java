package es.onebox.event.seasontickets.amqp.renewals.purge;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurgeRenewalSeatsRoute extends RouteBuilder {

    @Autowired
    private PurgeRenewalSeatsConfiguration config;

    @Autowired
    private PurgeRenewalSeatsProcessor processor;

    @Override
    public void configure() {
        from(config.getRouteURL())
                .id(config.getName())
                .autoStartup(false)
                .process(processor);
    }
}
