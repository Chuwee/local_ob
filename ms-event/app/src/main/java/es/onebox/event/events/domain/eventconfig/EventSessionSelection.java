package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.events.enums.SessionSelectType;

import java.io.Serial;
import java.io.Serializable;

public class EventSessionSelection implements Serializable {

    @Serial
    private static final long serialVersionUID = -4792200179713529478L;

    private SessionSelectType type;
    private Boolean restrictType;
    private Boolean showAvailability;
    private SessionCalendar calendar;
    private SessionList list;

    public EventSessionSelection() {
    }

    public EventSessionSelection(SessionSelectType type, Boolean restrictType, SessionCalendar calendar, SessionList list) {
        this.type = type;
        this.restrictType = restrictType;
        this.calendar = calendar;
        this.list = list;
    }

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

    public SessionCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(SessionCalendar calendar) {
        this.calendar = calendar;
    }

    public SessionList getList() {
        return list;
    }

    public void setList(SessionList list) {
        this.list = list;
    }

    public Boolean getShowAvailability() { return showAvailability; }
    public void setShowAvailability(Boolean showAvailability) { this.showAvailability = showAvailability; }
}
