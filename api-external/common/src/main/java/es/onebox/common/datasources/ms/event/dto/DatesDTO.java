package es.onebox.common.datasources.ms.event.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class DatesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6402088285033414882L;

    private DateDTO start;
    private DateDTO end;

    public DateDTO getStart() {
        return start;
    }

    public void setStart(DateDTO start) {
        this.start = start;
    }

    public DateDTO getEnd() {
        return end;
    }

    public void setEnd(DateDTO end) {
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
