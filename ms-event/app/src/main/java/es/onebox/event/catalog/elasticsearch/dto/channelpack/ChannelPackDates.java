package es.onebox.event.catalog.elasticsearch.dto.channelpack;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelPackDates implements Serializable {

    @Serial
    private static final long serialVersionUID = -3197284929374427490L;

    private ZonedDateTime start;
    private ZonedDateTime end;
    private ZonedDateTime saleStart;
    private ZonedDateTime saleEnd;
    private Boolean startUnconfirmed;

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public ZonedDateTime getSaleStart() {
        return saleStart;
    }

    public void setSaleStart(ZonedDateTime saleStart) {
        this.saleStart = saleStart;
    }

    public ZonedDateTime getSaleEnd() {
        return saleEnd;
    }

    public void setSaleEnd(ZonedDateTime saleEnd) {
        this.saleEnd = saleEnd;
    }

    public Boolean getStartUnconfirmed() {
        return startUnconfirmed;
    }

    public void setStartUnconfirmed(Boolean startUnconfirmed) {
        this.startUnconfirmed = startUnconfirmed;
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
