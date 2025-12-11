package es.onebox.event.catalog;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.io.Serial;

public class EventMigrationMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = 34356364782L;

    private Long eventId;
    private Long sessionId;
    private boolean allSessions;
    private String origin;
    private String refreshType;
    private String enqueueTime;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAllSessions() {
        return allSessions;
    }

    public void setAllSessions(boolean allSessions) {
        this.allSessions = allSessions;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRefreshType() {
        return refreshType;
    }

    public void setRefreshType(String refreshType) {
        this.refreshType = refreshType;
    }

    public String getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(String enqueueTime) {
        this.enqueueTime = enqueueTime;
    }
}
