package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SettingsAccessControlDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("admission_dates")
    private SettingsAccessControlDatesDTO dates;

    private SettingsAccessControlSpaceDTO space;

    public SettingsAccessControlDatesDTO getDates() {
        return dates;
    }

    public void setDates(SettingsAccessControlDatesDTO dates) {
        this.dates = dates;
    }

    public SettingsAccessControlSpaceDTO getSpace() {
        return space;
    }

    public void setSpace(SettingsAccessControlSpaceDTO space) {
        this.space = space;
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
