package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.events.enums.SessionCalendarSelectType;
import es.onebox.event.events.enums.SessionCalendarType;
import es.onebox.event.events.enums.SessionSelectType;

import java.io.Serial;
import java.io.Serializable;

public class SessionCalendar implements Serializable {

    @Serial
    private static final long serialVersionUID = 7489633666192104450L;

    private SessionCalendarType calendarType;
    private SessionCalendarSelectType sessionSelectType;
    private Boolean enabled;

    public SessionCalendar() {
    }

    public SessionCalendar(SessionCalendarType calendarType) {
        this.calendarType = calendarType;
    }

    public SessionCalendarType getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(SessionCalendarType calendarType) {
        this.calendarType = calendarType;
    }

    public SessionCalendarSelectType getSessionSelectType() { return sessionSelectType; }

    public void setSessionSelectType(SessionCalendarSelectType sessionSelectType) { this.sessionSelectType = sessionSelectType; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
