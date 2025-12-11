package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelReviews implements Serializable {

    @Serial
    private static final long serialVersionUID = 5257529709531596769L;

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}