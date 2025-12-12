package es.onebox.ms.notification.externalnotifications.event;

import java.io.Serializable;
import java.util.List;

public class ExternalEventConsumeNotificationMessage implements Serializable {

    private Integer eventId;
    private Integer channelId;
    private Boolean forceNotification;
    private List<Integer> sessions;
    private EventCriteria oldEvent;
    private EventCriteria newEvent;

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

    public EventCriteria getOldEvent() {
        return oldEvent;
    }

    public void setOldEvent(EventCriteria oldEvent) {
        this.oldEvent = oldEvent;
    }

    public EventCriteria getNewEvent() {
        return newEvent;
    }

    public void setNewEvent(EventCriteria newEvent) {
        this.newEvent = newEvent;
    }

    public Boolean getForceNotification() {
        return forceNotification == null ? false: true;
    }

    public void setForceNotification(Boolean forceNotification) {
        this.forceNotification = forceNotification;
    }
}
