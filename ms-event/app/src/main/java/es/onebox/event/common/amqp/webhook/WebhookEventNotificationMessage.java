package es.onebox.event.common.amqp.webhook;

import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

public class WebhookEventNotificationMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long channelId;

    private String event;

    private NotificationSubtype notificationSubtype;

    public WebhookEventNotificationMessage(Long id, Long channelId, String event, NotificationSubtype notificationSubtype) {
        this.id = id;
        this.channelId = channelId;
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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public NotificationSubtype getNotificationSubtype() {
        return notificationSubtype;
    }

    public void setNotificationSubtype(NotificationSubtype notificationSubtype) {
        this.notificationSubtype = notificationSubtype;
    }
}
