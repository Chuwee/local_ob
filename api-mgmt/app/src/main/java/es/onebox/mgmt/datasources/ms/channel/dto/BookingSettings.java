package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serializable;
import java.util.List;

public class BookingSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allowBooking;
    private Boolean allowBookingCheckout;
    private String bookingCheckoutDomain;
    private List<String> bookingCheckoutPaymentMethods;
    private Boolean allowCustomerAssignation;
    private Boolean allowPresaleRestrictions;

    public Boolean getAllowCustomerAssignation() {
        return allowCustomerAssignation;
    }

    public void setAllowCustomerAssignation(Boolean allowCustomerAssignation) {
        this.allowCustomerAssignation = allowCustomerAssignation;
    }

    public Boolean getAllowBooking() {
        return allowBooking;
    }

    public void setAllowBooking(Boolean allowBooking) {
        this.allowBooking = allowBooking;
    }

    public Boolean getAllowBookingCheckout() {
        return allowBookingCheckout;
    }

    public void setAllowBookingCheckout(Boolean allowBookingCheckout) {
        this.allowBookingCheckout = allowBookingCheckout;
    }

    public List<String> getBookingCheckoutPaymentMethods() {
        return bookingCheckoutPaymentMethods;
    }

    public void setBookingCheckoutPaymentMethods(List<String> bookingCheckoutPaymentMethods) {
        this.bookingCheckoutPaymentMethods = bookingCheckoutPaymentMethods;
    }

    public String getBookingCheckoutDomain() {
        return bookingCheckoutDomain;
    }

    public void setBookingCheckoutDomain(String bookingCheckoutDomain) {
        this.bookingCheckoutDomain = bookingCheckoutDomain;
    }

    public Boolean getAllowPresaleRestrictions() {
        return allowPresaleRestrictions;
    }

    public void setAllowPresaleRestrictions(Boolean allowPresaleRestrictions) {
        this.allowPresaleRestrictions = allowPresaleRestrictions;
    }
}
