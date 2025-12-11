package es.onebox.event.events.dto;

import es.onebox.event.events.enums.SessionSelectType;

import java.io.Serial;
import java.io.Serializable;

public class EventSessionSelectionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4792200179713529478L;

    private SessionSelectType type;
    private Boolean restrictType;
    private Boolean showAvailability;
    private SessionCalendarDTO calendar;
    private SessionListDTO list;

    public EventSessionSelectionDTO() {
    }

    public EventSessionSelectionDTO(SessionSelectType type, Boolean restrictType,
                                    SessionCalendarDTO calendar, SessionListDTO list,
                                    Boolean showAvailability) {
        this.type = type;
        this.restrictType = restrictType;
        this.calendar = calendar;
        this.list = list;
        this.showAvailability = showAvailability;
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

    public SessionCalendarDTO getCalendar() {
        return calendar;
    }

    public void setCalendar(SessionCalendarDTO calendar) {
        this.calendar = calendar;
    }

    public SessionListDTO getList() {
        return list;
    }

    public void setList(SessionListDTO list) {
        this.list = list;
    }

    public Boolean getShowAvailability() { return showAvailability; }

    public void setShowAvailability(Boolean showAvailability) { this.showAvailability = showAvailability; }
}
