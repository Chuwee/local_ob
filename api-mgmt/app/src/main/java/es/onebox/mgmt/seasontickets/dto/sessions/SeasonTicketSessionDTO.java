package es.onebox.mgmt.seasontickets.dto.sessions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketAssignationStatus;
import es.onebox.mgmt.timezone.dto.TimeZoneDTO;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SeasonTicketSessionDTO implements Serializable, DateConvertible {
    private static final long serialVersionUID = 2976880878664724547L;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("session_name")
    private String sessionName;

    @JsonProperty("event_id")
    private Long eventId;

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("session_assignable")
    private SessionAssignable sessionAssignable;

    @JsonProperty("session_starting_date")
    private ZonedDateTime sessionStartingDate;

    @JsonIgnore
    private TimeZoneDTO sessionStartingDateTZ;

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

    public TimeZoneDTO getSessionStartingDateTZ() {
        return sessionStartingDateTZ;
    }

    public void setSessionStartingDateTZ(TimeZoneDTO sessionStartingDateTZ) {
        this.sessionStartingDateTZ = sessionStartingDateTZ;
    }

    public SeasonTicketAssignationStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketAssignationStatus status) {
        this.status = status;
    }

    @Override
    public void convertDates() {
        if (sessionStartingDate != null && sessionStartingDateTZ != null) {
            this.sessionStartingDate = this.sessionStartingDate.withZoneSameInstant(ZoneId.of(sessionStartingDateTZ.getOlsonId()));
        }
    }
}
