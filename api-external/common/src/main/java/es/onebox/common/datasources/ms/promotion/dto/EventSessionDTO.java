package es.onebox.common.datasources.ms.promotion.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.ms.event.dto.SessionDateDTO;
import es.onebox.common.datasources.ms.event.enums.SessionType;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventSessionDTO extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    private Long eventId;

    @JsonProperty("type")
    private SessionType sessionType;
    
    @JsonProperty("dates")
    private SessionDateDTO date;

    public EventSessionDTO() {
    }

    public EventSessionDTO(Long id) {
        super(id);
    }

    public EventSessionDTO(Long id, String name, Integer sessionType, SessionDateDTO date) {
        super(id, name);
        this.sessionType = SessionType.getById(sessionType);
        this.date = date;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public SessionDateDTO getDate() {
        return date;
    }

    public void setDate(SessionDateDTO date) {
        this.date = date;
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
