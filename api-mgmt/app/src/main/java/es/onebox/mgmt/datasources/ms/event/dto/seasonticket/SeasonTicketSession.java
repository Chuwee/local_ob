package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import es.onebox.mgmt.seasontickets.dto.sessions.SessionAssignable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketSession implements Serializable {
    private static final long serialVersionUID = 6644679195330464693L;

    private Long sessionId;

    private String sessionName;

    private Long eventId;

    private String eventName;

    private SessionAssignable sessionAssignable;

    @JsonProperty("beginSessionDate")
    private ZonedDateTime sessionStartingDate;

    @JsonProperty("beginSessionDateTZ")
    private TimeZone sessionStartingDateTZ;

    private SeasonTicketAssignationStatus status;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public SessionAssignable getSessionAssignable() {
        return sessionAssignable;
    }

    public void setSessionAssignable(SessionAssignable sessionAssignable) {
        this.sessionAssignable = sessionAssignable;
    }

    public ZonedDateTime getSessionStartingDate() {
        return sessionStartingDate;
    }

    public void setSessionStartingDate(ZonedDateTime sessionStartingDate) {
        this.sessionStartingDate = sessionStartingDate;
    }

    public TimeZone getSessionStartingDateTZ() {
        return sessionStartingDateTZ;
    }

    public void setSessionStartingDateTZ(TimeZone sessionStartingDateTZ) {
        this.sessionStartingDateTZ = sessionStartingDateTZ;
    }

    public SeasonTicketAssignationStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketAssignationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
