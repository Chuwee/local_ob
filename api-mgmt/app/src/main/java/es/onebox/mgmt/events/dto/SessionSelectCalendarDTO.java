package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.SessionCalendarSelectType;
import es.onebox.mgmt.events.enums.SessionCalendarType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SessionSelectCalendarDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1203958319382945460L;

    private SessionCalendarType type;
    @JsonProperty("session_select")
    private SessionCalendarSelectType sessionCalendarSelectType;
    private Boolean enabled;

    public SessionCalendarType getType() {
        return type;
    }

    public void setType(SessionCalendarType type) {
        this.type = type;
    }

    public SessionCalendarSelectType getSessionCalendarSelectType() {
        return sessionCalendarSelectType;
    }

    public void setSessionCalendarSelectType(SessionCalendarSelectType sessionCalendarSelectType) {
        this.sessionCalendarSelectType = sessionCalendarSelectType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
