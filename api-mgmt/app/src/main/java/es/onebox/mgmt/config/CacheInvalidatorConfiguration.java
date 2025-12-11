package es.onebox.mgmt.config;

import es.onebox.mgmt.common.cache.invalidation.orders.OrdersCachedInvalidatorConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheInvalidatorConfiguration {

    @Bean
    public OrdersCachedInvalidatorConsumer ordersCachedRepositoryInvalidatorConsumer(
            @Value("${amqp.orders-invalidation.exchange}") final String exchangeName,
            @Value("${amqp.orders-invalidation.name}") final String queueName) {
        return new OrdersCachedInvalidatorConsumer(exchangeName, queueName);
    }

}

