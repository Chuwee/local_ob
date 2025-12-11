package es.onebox.event.seasontickets.amqp.renewals.revert;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.renewal-revert-seat")
public class RevertRenewalConfiguration extends AbstractQueueConfiguration {
}
