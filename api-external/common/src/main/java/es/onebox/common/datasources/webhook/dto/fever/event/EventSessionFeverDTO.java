package es.onebox.common.datasources.webhook.dto.fever.event;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.enums.SessionType;
import es.onebox.common.datasources.webhook.dto.fever.session.SessionDateFeverDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class EventSessionFeverDTO extends IdNameDTO {

    private static final long serialVersionUID = 1L;

    private Long eventId;

    @JsonProperty("type")
    private SessionType sessionType;
    
    @JsonProperty("dates")
    private SessionDateFeverDTO date;

    public EventSessionFeverDTO() {
    }

    public EventSessionFeverDTO(Long id) {
        super(id);
    }

    public EventSessionFeverDTO(Long id, String name, Integer sessionType, SessionDateFeverDTO date) {
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

    public SessionDateFeverDTO getDate() {
        return date;
    }

    public void setDate(SessionDateFeverDTO date) {
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
