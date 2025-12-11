package es.onebox.mgmt.channels.dto;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelReviewsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6123261100762445834L;

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
