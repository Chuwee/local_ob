package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.ChangeSessionType;
import es.onebox.mgmt.events.enums.SessionSelectType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventUISeatSelectionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -777106768775591044L;

    @JsonProperty("change_session")
    private ChangeSessionType changeSessionSelectType;
    @JsonProperty("restrict_selection_type")
    private Boolean restrictType;
    @JsonProperty("type")
    private SessionSelectType type;
    @JsonProperty("show_availability")
    private Boolean showAvailability;
    @JsonProperty("calendar")
    private SessionSelectCalendarDTO calendar;
    @JsonProperty("list")
    private SessionSelectListDTO list;



    public ChangeSessionType getChangeSessionSelectType() {
        return changeSessionSelectType;
    }

    public void setChangeSessionSelectType(ChangeSessionType changeSessionSelectType) {
        this.changeSessionSelectType = changeSessionSelectType;
    }

    public Boolean getShowAvailability() {
        return showAvailability;
    }

    public void setShowAvailability(Boolean showAvailability) {
        this.showAvailability = showAvailability;
    }

    public SessionSelectCalendarDTO getCalendar() {
        return calendar;
    }

    public void setCalendar(SessionSelectCalendarDTO calendar) {
        this.calendar = calendar;
    }

    public SessionSelectListDTO getList() {
        return list;
    }

    public void setList(SessionSelectListDTO list) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
