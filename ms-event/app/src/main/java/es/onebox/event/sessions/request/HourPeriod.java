package es.onebox.event.sessions.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.OffsetTime;

public class HourPeriod implements Serializable {

    private static final long serialVersionUID = 1L;

    private OffsetTime from;
    private OffsetTime to;

    public HourPeriod(OffsetTime from, OffsetTime to) {
        this.from = from;
        this.to = to;
    }

    public OffsetTime getFrom() {
        return from;
    }

    public void setFrom(OffsetTime from) {
        this.from = from;
    }

    public OffsetTime getTo() {
        return to;
    }

    public void setTo(OffsetTime to) {
        this.to = to;
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
