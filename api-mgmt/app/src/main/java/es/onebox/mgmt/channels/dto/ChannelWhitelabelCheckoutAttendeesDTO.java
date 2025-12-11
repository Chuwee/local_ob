package es.onebox.mgmt.channels.dto;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelCheckoutAttendeesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8728031804405640930L;

    private Boolean autofill;

    public Boolean getAutofill() {
        return autofill;
    }

    public void setAutofill(Boolean autofill) {
        this.autofill = autofill;
    }
}
