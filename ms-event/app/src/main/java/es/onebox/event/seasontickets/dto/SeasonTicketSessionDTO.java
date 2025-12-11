package es.onebox.event.seasontickets.dto;

import es.onebox.event.events.dto.TimeZoneDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SeasonTicketSessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 699697805296511291L;


    private Integer sessionId;

    private String sessionName;

    private Integer eventId;

    private String eventName;

    private SessionAssignableDTO sessionAssignable;

    private ZonedDateTime beginSessionDate;
    private ZonedDateTime realEndSessionDate;

    private TimeZoneDTO beginSessionDateTZ;

    private SessionAssignationStatusDTO status;

    private List<SeasonTicketSessionCommunicationElement> communicationElements;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public SessionAssignableDTO getSessionAssignable() {
        return sessionAssignable;
    }

    public void setSessionAssignable(SessionAssignableDTO sessionAssignable) {
        this.sessionAssignable = sessionAssignable;
    }

    public ZonedDateTime getBeginSessionDate() {
        return beginSessionDate;
    }

    public void setBeginSessionDate(ZonedDateTime beginSessionDate) {
        this.beginSessionDate = beginSessionDate;
    }

    public ZonedDateTime getRealEndSessionDate() {
        return realEndSessionDate;
    }

    public void setRealEndSessionDate(ZonedDateTime realEndSessionDate) {
        this.realEndSessionDate = realEndSessionDate;
    }

    public TimeZoneDTO getBeginSessionDateTZ() {
        return beginSessionDateTZ;
    }

    public void setBeginSessionDateTZ(TimeZoneDTO beginSessionDateTZ) {
        this.beginSessionDateTZ = beginSessionDateTZ;
    }

    public SessionAssignationStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SessionAssignationStatusDTO status) {
        this.status = status;
    }

    public List<SeasonTicketSessionCommunicationElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<SeasonTicketSessionCommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
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
