package es.onebox.event.attendants.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ModifySessionAttendantsConfigDTO extends AttendantsConfigDTO {

    @Serial
    private static final long serialVersionUID = -7292154783868337355L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
