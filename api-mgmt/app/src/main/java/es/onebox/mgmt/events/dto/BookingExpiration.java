package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.event.dto.event.TypeDeadlineExpiration;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class BookingExpiration implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("booking_order")
    private BookingOrderExpiration bookingOrderExpiration;

    @JsonProperty("session")
    private BookingSessionExpiration bookingSessionExpiration;

    @JsonProperty("deadline_expiration_type")
    private TypeDeadlineExpiration expirationDeadlineType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime date;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public BookingOrderExpiration getBookingOrderExpiration() {
        return bookingOrderExpiration;
    }

    public void setBookingOrderExpiration(BookingOrderExpiration bookingOrderExpiration) {
        this.bookingOrderExpiration = bookingOrderExpiration;
    }

    public BookingSessionExpiration getBookingSessionExpiration() {
        return bookingSessionExpiration;
    }

    public void setBookingSessionExpiration(BookingSessionExpiration bookingSessionExpiration) {
        this.bookingSessionExpiration = bookingSessionExpiration;
    }

    public TypeDeadlineExpiration getExpirationDeadlineType() {
        return expirationDeadlineType;
    }

    public void setExpirationDeadlineType(TypeDeadlineExpiration expirationDeadlineType) {
        this.expirationDeadlineType = expirationDeadlineType;
    }
}
