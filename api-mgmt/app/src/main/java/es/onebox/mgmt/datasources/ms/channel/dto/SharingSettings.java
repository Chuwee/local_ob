package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serializable;

public class SharingSettings implements Serializable {

    private static final long serialVersionUID = 2154434985278695437L;

    private Boolean allowBookingSharing;
    private BookingCheckoutSettings bookingCheckout;

    public Boolean getAllowBookingSharing() {
        return allowBookingSharing;
    }

    public void setAllowBookingSharing(Boolean allowBookingSharing) {
        this.allowBookingSharing = allowBookingSharing;
    }

    public BookingCheckoutSettings getBookingCheckout() {
        return bookingCheckout;
    }

    public void setBookingCheckout(BookingCheckoutSettings bookingCheckout) {
        this.bookingCheckout = bookingCheckout;
    }
}
