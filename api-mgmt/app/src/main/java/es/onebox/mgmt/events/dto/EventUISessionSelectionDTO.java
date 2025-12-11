package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.SessionSelectType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventUISessionSelectionDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = -6006981912494722236L;

    @JsonProperty("type")
    private SessionSelectType sessionSelectType;
    @JsonProperty("restrict_selection_type")
    private Boolean restrictSelectionType;
    @JsonProperty("show_availability")
    private Boolean showAvailability;
    @JsonProperty("calendar")
    private SessionSelectCalendarDTO sessionSelectCalendar;
    @JsonProperty("list")
    private SessionSelectListDTO sessionSelectList;

    public SessionSelectType getSessionSelectType() {
        return sessionSelectType;
    }

    public void setSessionSelectType(SessionSelectType sessionSelectType) {
        this.sessionSelectType = sessionSelectType;
    }

    public Boolean getRestrictSelectionType() {
        return restrictSelectionType;
    }

    public void setRestrictSelectionType(Boolean restrictSelectionType) {
        this.restrictSelectionType = restrictSelectionType;
    }

    public SessionSelectCalendarDTO getSessionSelectCalendar() {
        return sessionSelectCalendar;
    }

    public void setSessionSelectCalendar(SessionSelectCalendarDTO sessionSelectCalendarDTO) {
        this.sessionSelectCalendar = sessionSelectCalendarDTO;
    }

    public SessionSelectListDTO getSessionSelectList() {
        return sessionSelectList;
    }

    public void setSessionSelectList(SessionSelectListDTO sessionSelectList) {
        this.sessionSelectList = sessionSelectList;
    }

    public Boolean getShowAvailability() { return showAvailability; }
    public void setShowAvailability(Boolean showAvailability) { this.showAvailability = showAvailability; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
