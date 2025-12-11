package es.onebox.event.seasontickets.amqp.renewals.refund;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.renewal-refund")
public class RefundRenewalsConfiguration extends AbstractQueueConfiguration {
}
