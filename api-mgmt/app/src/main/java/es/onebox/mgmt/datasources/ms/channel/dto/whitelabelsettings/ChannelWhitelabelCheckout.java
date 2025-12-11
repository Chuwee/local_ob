package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import es.onebox.mgmt.channels.enums.CheckoutFlow;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelCheckout implements Serializable {

    @Serial
    private static final long serialVersionUID = 4861176510434098270L;

    private CheckoutFlow checkoutFlow;
    private ChannelWhitelabelCheckoutAttendees attendees;

    public CheckoutFlow getCheckoutFlow() {
        return checkoutFlow;
    }

    public void setCheckoutFlow(CheckoutFlow checkoutFlow) {
        this.checkoutFlow = checkoutFlow;
    }

    public ChannelWhitelabelCheckoutAttendees getAttendees() {
        return attendees;
    }

    public void setAttendees(ChannelWhitelabelCheckoutAttendees attendees) {
        this.attendees = attendees;
    }
}
