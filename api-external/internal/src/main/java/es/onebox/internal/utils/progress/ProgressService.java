package es.onebox.internal.utils.progress;

import es.onebox.internal.utils.progress.enums.ConsumerType;
import es.onebox.internal.utils.progress.enums.StatusMessage;
import es.onebox.internal.utils.progress.model.ProgressMessage;
import es.onebox.message.broker.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProgressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressService.class);

    private final MessageProducer notificationProgressProducer;

    @Autowired
    public ProgressService(MessageProducer notificationProgressProducer) {
        this.notificationProgressProducer = notificationProgressProducer;
    }

    public void sendNotificationProgress(ProgressMessage progressMessage, Integer progress, StatusMessage status, ConsumerType type) {
        fill(progressMessage, progress, status);
        send(progressMessage, type);
    }

    private void send(ProgressMessage progressMessage, ConsumerType type) {
        try {
            LOGGER.info("ConsumerType {} : Send notification {}, progress: {}", type, progressMessage, progressMessage.getProgress());

            Map<String, Object> headers = new HashMap<>();
            headers.put("consumerType", type.toString());

            notificationProgressProducer.sendMessage(progressMessage, headers);
        } catch (Exception e) {
            LOGGER.error("Error sending message type: {} , routingKey: {}" , progressMessage.getClass().getName() , progressMessage.getRoutingKey(), e);
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
