package es.onebox.ms.notification.ie.orderrelease;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.ie-order-release")
public class OrderReleaseConfiguration extends AbstractQueueConfiguration {

    @Bean
    public OrderReleaseRoute orderReleaseRoute() {
        return new OrderReleaseRoute();
    }

    @Bean
    public OrderReleaseProcessor orderReleaseProcessor() {
        return new OrderReleaseProcessor();
    }

    @Bean
    public OrderReleaseService orderReleaseService() {
        return new OrderReleaseService();
    }

}
