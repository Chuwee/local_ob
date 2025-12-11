package es.onebox.event.attendants.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class EventAttendantsConfigDTO extends AttendantsConfigDTO {

    @Serial
    private static final long serialVersionUID = 4069966536465270677L;

    private Long eventId;
    private Boolean allowAttendantsModification;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Boolean getAllowAttendantsModification() {
        return allowAttendantsModification;
    }

    public void setAllowAttendantsModification(Boolean allowAttendantsModification) {
        this.allowAttendantsModification = allowAttendantsModification;
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
