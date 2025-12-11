package es.onebox.event.seasontickets.amqp.session;

import es.onebox.message.broker.eip.configuration.RabbitMQEIPConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateSeasonTicketSessionRoute extends RouteBuilder {

    private static final int REDELIVERY_DELAY = 20000;
    private static final int MAX_REDELIVERIES = 10;

    @Autowired
    private CreateSeasonTicketSessionConfiguration configuration;

    @Autowired
    private CreateSeasonTicketSessionProcessor createSeasonTicketSessionProcessor;

    @Override
    public void configure() {
        from(configuration.getRouteURL())
                .id(configuration.getName()).autoStartup(Boolean.FALSE)
                .errorHandler(deadLetterChannel(RabbitMQEIPConfiguration.LOG_DEADLETTER_URI)
                        .redeliveryDelay(REDELIVERY_DELAY)
                        .maximumRedeliveries(MAX_REDELIVERIES))
                .process(createSeasonTicketSessionProcessor);
    }

}
