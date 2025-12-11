package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.events.enums.ChangeSessionType;
import es.onebox.mgmt.events.enums.SessionSelectType;

import java.io.Serial;
import java.io.Serializable;

public class EventSeatSelection implements Serializable {
    @Serial
    private static final long serialVersionUID = 3036685076498697769L;
    private ChangeSessionType changeSessionSelectType;
    private Boolean restrictType;
    private SessionSelectType type;
    private Boolean showAvailability;
    private SessionCalendar calendar;
    private SessionList list;

    public ChangeSessionType getChangeSessionSelectType() { return changeSessionSelectType; }
    public void setChangeSessionSelectType(ChangeSessionType changeSessionSelectType) {
        this.changeSessionSelectType = changeSessionSelectType;
    }

    public Boolean getShowAvailability() { return showAvailability; }
    public void setShowAvailability(Boolean showAvailability) { this.showAvailability = showAvailability; }

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

    public Boolean getRestrictType() {
        return restrictType;
    }

    public void setRestrictType(Boolean restrictType) {
        this.restrictType = restrictType;
    }

    public SessionSelectType getType() {
        return type;
    }

    public void setType(SessionSelectType type) {
        this.type = type;
    }
}
