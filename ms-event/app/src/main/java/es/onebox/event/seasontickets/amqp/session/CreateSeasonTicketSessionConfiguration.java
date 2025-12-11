package es.onebox.event.seasontickets.amqp.session;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.create-season-ticket-session")
public class CreateSeasonTicketSessionConfiguration extends AbstractQueueConfiguration {

}
