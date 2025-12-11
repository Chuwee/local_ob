package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class BookingSettingsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enable;

    @JsonProperty("expiration")
    private BookingExpiration bookingExpiration;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public BookingExpiration getBookingExpiration() {
        return bookingExpiration;
    }

    public void setBookingExpiration(BookingExpiration bookingExpiration) {
        this.bookingExpiration = bookingExpiration;
    }

}
