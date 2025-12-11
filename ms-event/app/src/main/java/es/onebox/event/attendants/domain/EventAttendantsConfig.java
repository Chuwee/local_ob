package es.onebox.event.attendants.domain;

import es.onebox.couchbase.annotations.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class EventAttendantsConfig extends AttendantsConfig {

    @Serial
    private static final long serialVersionUID = -7173834801814937881L;

    @Id
    private Long eventId;

    private List<Long> customConfiguredSessions;

    private Boolean allowAttendantsModification;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public List<Long> getCustomConfiguredSessions() {
        return customConfiguredSessions;
    }

    public void setCustomConfiguredSessions(List<Long> customConfiguredSessions) {
        this.customConfiguredSessions = customConfiguredSessions;
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
