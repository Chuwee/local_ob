package es.onebox.atm.tickets.eip;

import es.onebox.atm.config.ATMTicketQueueConfig;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ATMTicketRoute extends RouteBuilder {

    @Autowired
    private ATMTicketQueueConfig atmTicketQueueConfig;

    @Autowired
    private ATMTicketProcessor atmTicketProcessor;

    @Override
    public void configure() {
        from(atmTicketQueueConfig.getRouteURL())
                .id(atmTicketQueueConfig.getName())
                .process(atmTicketProcessor);
    }
}
