package es.onebox.event.catalog.dto;

import java.io.Serializable;

public class ChangeSeatVoucherExpiry implements Serializable {

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
