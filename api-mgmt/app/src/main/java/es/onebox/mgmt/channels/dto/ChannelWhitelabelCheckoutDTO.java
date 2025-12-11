package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.CheckoutFlow;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelCheckoutDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -34926809646734593L;

    @JsonProperty("checkout_flow")
    private CheckoutFlow checkoutFlow;
    private ChannelWhitelabelCheckoutAttendeesDTO attendees;

    public CheckoutFlow getCheckoutFlow() {
        return checkoutFlow;
    }

    public void setCheckoutFlow(CheckoutFlow checkoutFlow) {
        this.checkoutFlow = checkoutFlow;
    }

    public ChannelWhitelabelCheckoutAttendeesDTO getAttendees() {
        return attendees;
    }

    public void setAttendees(ChannelWhitelabelCheckoutAttendeesDTO attendees) {
        this.attendees = attendees;
    }
}
