package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatVoucherExpiry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private ChangeSeatExpiryTime expiryTime;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ChangeSeatExpiryTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(ChangeSeatExpiryTime changeSeatExpiryTime) {
        this.expiryTime = changeSeatExpiryTime;
    }
}
