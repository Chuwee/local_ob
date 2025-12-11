package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelReviewConfigDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = -3559614684755862408L;

    private String eventName;
    private String sessionName;
    private ZonedDateTime sessionStartDate;
    private Long eventId;
    private Long sessionId;

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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public ZonedDateTime getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(ZonedDateTime sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }
}
