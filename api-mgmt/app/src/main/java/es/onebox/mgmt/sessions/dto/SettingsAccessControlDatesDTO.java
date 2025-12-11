package es.onebox.mgmt.sessions.dto;

import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SettingsAccessControlDatesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean override;

    private ZonedDateTimeWithRelative start;

    private ZonedDateTimeWithRelative end;

    public Boolean getOverride() {
        return override;
    }

    public void setOverride(Boolean override) {
        this.override = override;
    }

    public ZonedDateTimeWithRelative getStart() {
        return start;
    }

    public void setStart(ZonedDateTimeWithRelative start) {
        this.start = start;
    }

    public ZonedDateTimeWithRelative getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTimeWithRelative end) {
        this.end = end;
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
