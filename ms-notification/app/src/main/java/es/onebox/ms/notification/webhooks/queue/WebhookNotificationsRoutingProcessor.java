package es.onebox.ms.notification.webhooks.queue;

import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.ms.notification.common.dto.MemberOrderDTO;
import es.onebox.ms.notification.datasources.ms.order.repository.OrdersRepository;
import es.onebox.ms.notification.webhooks.WebhookService;
import es.onebox.ms.notification.webhooks.dto.EventNotificationMessage;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.enums.NotificationType;
import org.apache.camel.Exchange;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class WebhookNotificationsRoutingProcessor extends DefaultProcessor {

    private final DefaultProducer webhookNotificationOrderProducer;
    private final DefaultProducer webhookNotificationDefaultProducer;
    private final WebhookService webhookService;
    private final OrdersRepository ordersRepository;

    @Autowired
    public WebhookNotificationsRoutingProcessor(@Qualifier("webhookNotificationOrderProducer") DefaultProducer webhookNotificationOrderProducer,
                                                @Qualifier("webhookNotificationDefaultProducer") DefaultProducer webhookNotificationDefaultProducer,
                                                WebhookService webhookService,
                                                OrdersRepository ordersRepository){
        this.webhookNotificationOrderProducer = webhookNotificationOrderProducer;
        this.webhookNotificationDefaultProducer = webhookNotificationDefaultProducer;
        this.webhookService = webhookService;
        this.ordersRepository = ordersRepository;
    }

    @Override
    public void execute(Exchange exchange) throws Exception {
        EventNotificationMessage message = exchange.getIn().getBody(EventNotificationMessage.class);
        Map<String, Object> headers =exchange.getIn().getHeaders();
        NotificationType notificationType = NotificationType.fromString(message.getEvent());

        switch (notificationType) {
            case ORDER, MEMBERORDER -> webhookNotificationOrderProducer.sendMessage(message, headers);
            default -> webhookNotificationDefaultProducer.sendMessage(message, headers);
        }
    }
}
