package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelCheckoutAttendees implements Serializable {

    @Serial
    private static final long serialVersionUID = -8316614952179765683L;

    private Boolean autofill;

    public Boolean getAutofill() {
        return autofill;
    }

    public void setAutofill(Boolean autofill) {
        this.autofill = autofill;
    }
}
