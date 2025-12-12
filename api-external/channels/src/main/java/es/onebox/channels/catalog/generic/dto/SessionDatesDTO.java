package es.onebox.channels.catalog.generic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionDatesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime start;

    @JsonProperty("sale_start")
    private ZonedDateTime saleStart;

    @JsonProperty("sale_end")
    private ZonedDateTime saleEnd;

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
