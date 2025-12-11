package es.onebox.event.events.dto;

import es.onebox.event.events.enums.ChangeSessionType;
import es.onebox.event.events.enums.SessionSelectType;

import java.io.Serial;
import java.io.Serializable;

public class EventSeatSelectionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7849421640874382307L;

    private ChangeSessionType changeSessionSelectType;
    private SessionSelectType type;
    private Boolean restrictType;
    private Boolean showAvailability;
    private SessionCalendarDTO calendar;
    private SessionListDTO list;

    public EventSeatSelectionDTO(ChangeSessionType changeSessionSelectType,
                                 Boolean showAvailability, SessionCalendarDTO calendar,
                                 SessionListDTO list, SessionSelectType type,
                                 Boolean restrictType) {
        this.changeSessionSelectType = changeSessionSelectType;
        this.showAvailability = showAvailability;
        this.type = type;
        this.restrictType = restrictType;
        this.calendar = calendar;
        this.list = list;
    }

    public EventSeatSelectionDTO() {
    }

    public ChangeSessionType getChangeSessionSelectType() { return changeSessionSelectType; }
    public void setChangeSessionSelectType(ChangeSessionType changeSessionSelectType) {
        this.changeSessionSelectType = changeSessionSelectType;
    }

    public Boolean getShowAvailability() { return showAvailability; }
    public void setShowAvailability(Boolean showAvailability) {
        this.showAvailability = showAvailability;
    }
    public SessionCalendarDTO getCalendar() { return calendar; }
    public void setCalendar(SessionCalendarDTO calendar) { this.calendar = calendar; }

    public SessionListDTO getList() { return list;
    }
    public void setList(SessionListDTO list) { this.list = list; }

    public SessionSelectType getType() {
        return type;
    }

    public void setType(SessionSelectType type) {
        this.type = type;
    }

    public Boolean getRestrictType() {
        return restrictType;
    }

    public void setRestrictType(Boolean restrictType) {
        this.restrictType = restrictType;
    }
}
