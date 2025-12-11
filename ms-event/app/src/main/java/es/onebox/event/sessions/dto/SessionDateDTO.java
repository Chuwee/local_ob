package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionDateDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime end;

    private ZonedDateTimeWithRelative channelPublication;

    private ZonedDateTimeWithRelative bookingsStart;
    private ZonedDateTimeWithRelative bookingsEnd;

    private ZonedDateTimeWithRelative salesStart;
    private ZonedDateTimeWithRelative salesEnd;

    private ZonedDateTimeWithRelative admissionStart;
    private ZonedDateTimeWithRelative admissionEnd;

    private ZonedDateTimeWithRelative secondaryMarketStart;
    private ZonedDateTimeWithRelative secondaryMarketEnd;

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

    public ZonedDateTimeWithRelative getChannelPublication() {
        return channelPublication;
    }

    public void setChannelPublication(ZonedDateTimeWithRelative channelPublication) {
        this.channelPublication = channelPublication;
    }

    public ZonedDateTimeWithRelative getBookingsStart() {
        return bookingsStart;
    }

    public void setBookingsStart(ZonedDateTimeWithRelative bookingsStart) {
        this.bookingsStart = bookingsStart;
    }

    public ZonedDateTimeWithRelative getBookingsEnd() {
        return bookingsEnd;
    }

    public void setBookingsEnd(ZonedDateTimeWithRelative bookingsEnd) {
        this.bookingsEnd = bookingsEnd;
    }

    public ZonedDateTimeWithRelative getSalesStart() {
        return salesStart;
    }

    public void setSalesStart(ZonedDateTimeWithRelative salesStart) {
        this.salesStart = salesStart;
    }

    public ZonedDateTimeWithRelative getSalesEnd() {
        return salesEnd;
    }

    public void setSalesEnd(ZonedDateTimeWithRelative salesEnd) {
        this.salesEnd = salesEnd;
    }

    public ZonedDateTimeWithRelative getAdmissionStart() {
        return admissionStart;
    }

    public void setAdmissionStart(ZonedDateTimeWithRelative admissionStart) {
        this.admissionStart = admissionStart;
    }

    public ZonedDateTimeWithRelative getAdmissionEnd() {
        return admissionEnd;
    }

    public void setAdmissionEnd(ZonedDateTimeWithRelative admissionEnd) {
        this.admissionEnd = admissionEnd;
    }

    public ZonedDateTimeWithRelative getSecondaryMarketStart() {
        return secondaryMarketStart;
    }

    public void setSecondaryMarketStart(ZonedDateTimeWithRelative secondaryMarketStart) {
        this.secondaryMarketStart = secondaryMarketStart;
    }

    public ZonedDateTimeWithRelative getSecondaryMarketEnd() {
        return secondaryMarketEnd;
    }

    public void setSecondaryMarketEnd(ZonedDateTimeWithRelative secondaryMarketEnd) {
        this.secondaryMarketEnd = secondaryMarketEnd;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
