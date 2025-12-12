package es.onebox.eci.tickets;

import es.onebox.eci.tickets.config.ECITicketQueueConfig;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ECITicketRoute extends RouteBuilder {

    @Autowired
    private ECITicketQueueConfig ECITicketQueueConfig;

    @Autowired
    private ECITicketProcessor ECITicketProcessor;

    @Override
    public void configure() {
        from(ECITicketQueueConfig.getRouteURL())
                .id(ECITicketQueueConfig.getName())
                .process(ECITicketProcessor);
    }
}
