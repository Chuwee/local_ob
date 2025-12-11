package es.onebox.event.events.amqp.sessionarchiver;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

/**
 * @author ifernandez
 */
public class SessionArchiverMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private Long sessionId;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
