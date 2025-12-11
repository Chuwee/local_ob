package es.onebox.event.events.amqp.emailnotification;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public class EmailNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired
    @Qualifier("emailNotificationProducer")
    private DefaultProducer emailNotificationProducer;

    public EmailNotificationMessage sendEmailNotification(EmailNotificationMessage.NotificationType notificationType,
                                                          int userId, int eventChannelId, Map<String, String> notificationLiterals) {

        EmailNotificationMessage message = new EmailNotificationMessage();
        message.setNotificationType(notificationType);
        message.setUserId(userId);
        message.setEventChannelId(eventChannelId);
        message.setNotificationLiterals(notificationLiterals);

        try {
            emailNotificationProducer.sendMessage(message);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] EmailNotification Message could not be send", e);
        }
        return message;
    }

}
