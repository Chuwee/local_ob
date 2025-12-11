package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelResendTickets implements Serializable {

    @Serial
    private static final long serialVersionUID = -547843087023643642L;

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
