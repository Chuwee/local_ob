package es.onebox.event.events.dto;

import es.onebox.event.events.enums.SessionCalendarSelectType;
import es.onebox.event.events.enums.SessionCalendarType;

import java.io.Serial;
import java.io.Serializable;

public class SessionCalendarDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7489633666192104450L;

    private SessionCalendarType type;
    private SessionCalendarSelectType calendarSelectType;
    private Boolean enabled;

    public SessionCalendarDTO() {
    }

    public SessionCalendarDTO(SessionCalendarType type, SessionCalendarSelectType calendarSelectType, Boolean enabled) {
        this.type = type;
        this.calendarSelectType = calendarSelectType;
        this.enabled = enabled;
    }

    public SessionCalendarType getType() {
        return type;
    }

    public void setType(SessionCalendarType type) {
        this.type = type;
    }

    public SessionCalendarSelectType getCalendarSelectType() { return calendarSelectType; }
    public void setCalendarSelectType(SessionCalendarSelectType calendarSelectType) {
        this.calendarSelectType = calendarSelectType;
    }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
