package es.onebox.common.datasources.webhook.dto.fever.session;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@JsonNaming(SnakeCaseStrategy.class)
public class SessionDateFeverDTO implements Serializable {

    @Serial
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

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
