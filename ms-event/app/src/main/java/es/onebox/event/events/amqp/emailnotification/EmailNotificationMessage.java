package es.onebox.event.events.amqp.emailnotification;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.util.Map;

public class EmailNotificationMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 9387462343L;

    public enum NotificationType {
        SOLICITUD_EVENTO(1),
        VENTA_EVENTO(2),
        SOLICITUD_CANAL(3);
        int id;
        NotificationType(int id) {
            this.id = id;
        }
    }

    private Integer userId;
    private Integer eventChannelId;
    private NotificationType notificationType;
    private Map<String, String> notificationLiterals;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEventChannelId() {
        return eventChannelId;
    }

    public void setEventChannelId(Integer eventChannelId) {
        this.eventChannelId = eventChannelId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Map<String, String> getNotificationLiterals() {
        return notificationLiterals;
    }

    public void setNotificationLiterals(Map<String, String> notificationLiterals) {
        this.notificationLiterals = notificationLiterals;
    }
}
