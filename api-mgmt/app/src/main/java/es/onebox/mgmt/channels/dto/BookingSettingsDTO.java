package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BookingSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("allow_booking")
    private Boolean allowBooking;
    @JsonProperty("allow_booking_checkout")
    private Boolean allowBookingCheckout;
    @JsonProperty("booking_checkout_domain")
    private String bookingCheckoutDomain;
    @JsonProperty("booking_checkout_payment_methods")
    private List<String> bookingCheckoutPaymentMethods;
    @JsonProperty("allow_customer_assignation")
    private Boolean allowCustomerAssignation;
    @JsonProperty("allow_presale_restrictions")
    private Boolean allowPresaleRestrictions;


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

    public Boolean getAllowCustomerAssignation() {
        return allowCustomerAssignation;
    }

    public void setAllowCustomerAssignation(Boolean allowCustomerAssignation) {
        this.allowCustomerAssignation = allowCustomerAssignation;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
