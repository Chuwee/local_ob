package es.onebox.event.seasontickets.amqp.renewals.commit;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.renewal-commit")
public class CommitRenewalsConfiguration extends AbstractQueueConfiguration {
}
