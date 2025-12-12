package es.onebox.internal.automaticsales.eip.report;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.automatic-sales-report")
public class ReportQueueConfiguration extends AbstractQueueConfiguration {

}
