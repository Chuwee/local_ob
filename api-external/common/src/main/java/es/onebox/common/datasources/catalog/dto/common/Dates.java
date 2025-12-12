package es.onebox.common.datasources.catalog.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Dates implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime start;
    private ZonedDateTime end;
    @JsonProperty("sale_start")
    private ZonedDateTime saleStart;
    @JsonProperty("sale_end")
    private ZonedDateTime saleEnd;
    @JsonProperty("booking_start")
    private ZonedDateTime bookingStart;
    @JsonProperty("booking_end")
    private ZonedDateTime bookingEnd;

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

    public ZonedDateTime getBookingStart() {
        return bookingStart;
    }

    public void setBookingStart(ZonedDateTime bookingStart) {
        this.bookingStart = bookingStart;
    }

    public ZonedDateTime getBookingEnd() {
        return bookingEnd;
    }

    public void setBookingEnd(ZonedDateTime bookingEnd) {
        this.bookingEnd = bookingEnd;
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
