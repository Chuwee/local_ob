package es.onebox.mgmt.common.cache.invalidation.orders;

import com.rabbitmq.client.AMQP;
import es.onebox.message.broker.consumer.exchange.AbstractTopicMessageConsumer;
import es.onebox.message.broker.consumer.message.ConsumeMessage;
import es.onebox.mgmt.common.cache.enums.OrdersCachedMappingsType;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrdersCachedInvalidatorConsumer extends AbstractTopicMessageConsumer<OrdersCachedInvalidatorMessage> {

    @Autowired
    private OrdersRepository ordersRepository;

    public OrdersCachedInvalidatorConsumer(String exchangeName, String queueName) {
        super(exchangeName, queueName);
    }

    @Override
    public void consumeMessage(ConsumeMessage<OrdersCachedInvalidatorMessage> cacheInvalidatorMessage, AMQP.BasicProperties basicProperties) {
        if (cacheInvalidatorMessage == null || cacheInvalidatorMessage.getNotification() == null
                || cacheInvalidatorMessage.getNotification().getId() == null || cacheInvalidatorMessage.getNotification().getMappingType() == null) {
            return;
        }
        Long sessionCode = cacheInvalidatorMessage.getNotification().getId();
        OrdersCachedMappingsType mappingType = cacheInvalidatorMessage.getNotification().getMappingType();

        try {
            ordersRepository.invalidateCachedSessionsWithSales(sessionCode, mappingType);
        } catch (Exception e) {
            LOGGER.error("Error invalidating Sessions with sales cache with order: {}", sessionCode, e);
        }
    }
}
