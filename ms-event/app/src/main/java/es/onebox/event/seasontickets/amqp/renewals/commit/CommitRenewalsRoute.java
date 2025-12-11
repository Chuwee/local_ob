package es.onebox.event.seasontickets.amqp.renewals.commit;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommitRenewalsRoute extends RouteBuilder {

    @Autowired
    private CommitRenewalsConfiguration config;

    @Autowired
    private CommitRenewalsProcessor processor;

    @Override
    public void configure() {
        from(config.getRouteURL())
                .id(config.getName())
                .autoStartup(false)
                .process(processor);
    }
}
