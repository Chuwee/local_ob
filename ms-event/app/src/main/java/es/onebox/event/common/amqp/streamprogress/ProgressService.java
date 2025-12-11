package es.onebox.event.common.amqp.streamprogress;

import es.onebox.event.common.amqp.streamprogress.model.ProgressMessage;
import es.onebox.event.common.amqp.streamprogress.model.StatusMessage;
import es.onebox.message.broker.client.message.NotificationMessage;
import es.onebox.message.broker.producer.exchange.DefaultTopicProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class ProgressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressService.class);

    @Autowired
    private DefaultTopicProducer notificationProgressProducer;

    public void sendNotificationProgress(ProgressMessage progressMessage, Integer progress, StatusMessage status, ConsumerType type) {
        fill(progressMessage, progress, status);
        send(progressMessage, type);
    }

    private void send(NotificationMessage notificationMessage, ConsumerType type) {
        try {
            LOGGER.debug("ConsumerType {} : Send notification {}", type, notificationMessage);
            Map<String, Object> headers = new HashMap<>();
            headers.put("consumerType", type.toString());
            notificationProgressProducer.sendMessage(notificationMessage, headers);
        } catch (Exception e) {
            LOGGER.error("Error sending message type: " + notificationMessage.getClass().getName() + ", routingKey: " + notificationMessage.getRoutingKey(), e);
        }
    }

    private void fill(ProgressMessage progressMessage, Integer progress, StatusMessage status) {
        if (progressMessage != null) {
            if (progress != null) {
                progressMessage.setProgress(progress.byteValue());
            }
            if (status != null) {
                progressMessage.setStatus(status);
            }
        }
    }

}
