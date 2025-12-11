package es.onebox.event.events.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ChangeSeatVoucherExpiryDTO {

    @NotNull
    private Boolean enabled;

    @Valid
    private ChangeSeatExpiryTimeDTO expiryTime;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ChangeSeatExpiryTimeDTO getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(ChangeSeatExpiryTimeDTO expiryTime) {
        this.expiryTime = expiryTime;
    }
}
