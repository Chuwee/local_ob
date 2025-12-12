package es.onebox.ms.notification.ie.orderrefund;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.ie-order-refund")
public class OrderRefundConfiguration extends AbstractQueueConfiguration {

    @Bean
    public OrderRefundRoute orderRefundRoute() {
        return new OrderRefundRoute();
    }

    @Bean
    public OrderRefundProcessor orderRefundProcessor() {
        return new OrderRefundProcessor();
    }

    @Bean
    public OrderRefundService orderRefundService() {
        return new OrderRefundService();
    }

}
