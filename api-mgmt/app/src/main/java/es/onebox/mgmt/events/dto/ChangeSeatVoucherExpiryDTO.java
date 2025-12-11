package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatVoucherExpiryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "enabled cannot be null")
    private Boolean enabled;

    @Valid
    @JsonProperty("expiry_time")
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
