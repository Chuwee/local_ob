package es.onebox.event.events.amqp.eventnotification;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.util.List;

public class ExternalEventConsumeNotificationMessage extends AbstractNotificationMessage {

    private static final long serialVersionUID = 1L;

    private Integer eventId;
    private Integer channelId;
    private Boolean forceNotification;
    private List<Integer> sessions;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public List<Integer> getSessions() {
        return sessions;
    }

    public void setSessions(List<Integer> sessions) {
        this.sessions = sessions;
    }

    public Boolean getForceNotification() {
        return forceNotification;
    }

    public void setForceNotification(Boolean forceNotification) {
        this.forceNotification = forceNotification;
    }
}
