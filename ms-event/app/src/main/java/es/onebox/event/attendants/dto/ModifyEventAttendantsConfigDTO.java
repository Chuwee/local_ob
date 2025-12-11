package es.onebox.event.attendants.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ModifyEventAttendantsConfigDTO extends AttendantsConfigDTO {

    @Serial
    private static final long serialVersionUID = -8376405928924191597L;

    private Boolean allowAttendantsModification;

    public Boolean getAllowAttendantsModification() {
        return allowAttendantsModification;
    }

    public void setAllowAttendantsModification(Boolean allowAttendantsModification) {
        this.allowAttendantsModification = allowAttendantsModification;
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
