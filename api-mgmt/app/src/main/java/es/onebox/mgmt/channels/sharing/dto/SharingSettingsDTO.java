package es.onebox.mgmt.channels.sharing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SharingSettingsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2154434985278695437L;

    @JsonProperty("allow_booking_sharing")
    private Boolean allowBookingSharing;
    @JsonProperty("booking_checkout")
    private BookingCheckoutSettingsDTO bookingCheckout;

    public Boolean getAllowBookingSharing() {
        return allowBookingSharing;
    }

    public void setAllowBookingSharing(Boolean allowBookingSharing) {
        this.allowBookingSharing = allowBookingSharing;
    }

    public BookingCheckoutSettingsDTO getBookingCheckout() {
        return bookingCheckout;
    }

    public void setBookingCheckout(BookingCheckoutSettingsDTO bookingCheckout) {
        this.bookingCheckout = bookingCheckout;
    }
}
