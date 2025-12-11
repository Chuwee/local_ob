package es.onebox.mgmt.customdomains.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DomainSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3861545570185783794L;

    @NotNull(message = "Enabled field must not be null")
    private Boolean enabled;
    @NotNull(message = "Mode field must not be null")
    private DomainSettingsMode mode;
    @NotEmpty(message = "Allowed origins list must not be empty")
    @Size(max = 5, message = "Domains list must not exceed 5 elements")
    @Valid
    private List<@NotNull CustomDomainSetting> domains;

    public DomainSettingsDTO() {
    }

    public DomainSettingsDTO(Boolean enabled, DomainSettingsMode mode, List<CustomDomainSetting> domains) {
        this.enabled = enabled;
        this.mode = mode;
        this.domains = domains;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public DomainSettingsMode getMode() {
        return mode;
    }

    public void setMode(DomainSettingsMode mode) {
        this.mode = mode;
    }

    public List<CustomDomainSetting> getDomains() {
        return domains;
    }

    public void setDomains(List<CustomDomainSetting> domains) {
        this.domains = domains;
    }
}
