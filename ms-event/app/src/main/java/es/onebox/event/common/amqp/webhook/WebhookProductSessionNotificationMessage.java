package es.onebox.event.common.amqp.webhook;

import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

public class WebhookProductSessionNotificationMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long eventId;
    private String event;
    private NotificationSubtype notificationSubtype;

    public WebhookProductSessionNotificationMessage(Long productId, Long eventId, String event, NotificationSubtype notificationSubtype) {
        this.id = productId;
        this.eventId = eventId;
        this.event = event;
        this.notificationSubtype = notificationSubtype;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public NotificationSubtype getNotificationSubtype() {
        return notificationSubtype;
    }

    public void setNotificationSubtype(NotificationSubtype notificationSubtype) {
        this.notificationSubtype = notificationSubtype;
    }
}
