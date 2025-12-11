package es.onebox.event.events.amqp.eventremove;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

public class EventRemoveMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private int eventId;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

}
