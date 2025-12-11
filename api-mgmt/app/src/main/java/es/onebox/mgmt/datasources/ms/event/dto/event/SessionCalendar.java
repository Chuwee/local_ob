package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.events.enums.SessionCalendarSelectType;
import es.onebox.mgmt.events.enums.SessionCalendarType;

import java.io.Serial;
import java.io.Serializable;

public class SessionCalendar implements Serializable {

    @Serial
    private static final long serialVersionUID = 7489633666192104450L;

    private SessionCalendarType type;
    private SessionCalendarSelectType calendarSelectType;
    private Boolean enabled;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
