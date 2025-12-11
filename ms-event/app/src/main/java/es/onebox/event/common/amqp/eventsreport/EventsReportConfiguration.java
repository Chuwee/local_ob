package es.onebox.event.common.amqp.eventsreport;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.events-report")
public class EventsReportConfiguration extends AbstractQueueConfiguration {

}
