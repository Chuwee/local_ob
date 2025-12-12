package es.onebox.ms.notification.webhooks.dto;

import es.onebox.ms.notification.webhooks.enums.NotificationSubtype;

public class ChannelPayloadDTO extends PayloadRequest {

    private Long id;
    private NotificationSubtype subtype;
    private Long eventId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(NotificationSubtype subtype) {
        this.subtype = subtype;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
