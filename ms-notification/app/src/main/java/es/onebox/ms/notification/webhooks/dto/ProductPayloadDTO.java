package es.onebox.ms.notification.webhooks.dto;

import es.onebox.ms.notification.webhooks.enums.NotificationSubtype;

public class ProductPayloadDTO extends PayloadRequest{
    private NotificationSubtype notificationSubtype;
    private Long id;
    private Long eventId;
    private Long channelId;

    public NotificationSubtype getNotificationSubtype() {return notificationSubtype;}

    public void setNotificationSubtype(NotificationSubtype notificationSubtype) {this.notificationSubtype = notificationSubtype;}

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getEventId() { return eventId; }

    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getChannelId() { return channelId; }

    public void setChannelId(Long channelId) { this.channelId = channelId; }
}
