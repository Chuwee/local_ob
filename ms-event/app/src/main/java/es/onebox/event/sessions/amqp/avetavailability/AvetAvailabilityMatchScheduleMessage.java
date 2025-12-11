package es.onebox.event.sessions.amqp.avetavailability;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

public class AvetAvailabilityMatchScheduleMessage extends AbstractNotificationMessage implements NotificationMessage {

    private static final long serialVersionUID = 1L;

    private Integer matchId;
    private Integer sessionId;
    private Boolean create;

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getCreate() {
        return create;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }
}
