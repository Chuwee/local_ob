package es.onebox.ms.notification.webhooks.dto;

import es.onebox.ms.notification.webhooks.enums.NotificationAction;

import java.io.Serial;
import java.io.Serializable;

public abstract class PayloadRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2510752674138458799L;

    private String event;

    private NotificationAction action;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public NotificationAction getAction() {
        return action;
    }

    public void setAction(NotificationAction action) {
        this.action = action;
    }
}
