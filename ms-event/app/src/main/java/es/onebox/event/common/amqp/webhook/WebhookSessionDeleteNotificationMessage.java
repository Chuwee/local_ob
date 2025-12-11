package es.onebox.event.common.amqp.webhook;

import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

public class WebhookSessionDeleteNotificationMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long eventId;
    private String event;
    private NotificationSubtype notificationSubtype;


    public WebhookSessionDeleteNotificationMessage(Long id, Long eventId, String event, NotificationSubtype notificationSubtype) {
        this.id = id;
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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
