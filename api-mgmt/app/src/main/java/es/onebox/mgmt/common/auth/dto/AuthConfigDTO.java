package es.onebox.mgmt.common.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


public class AuthConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Boolean enabled;
    @JsonProperty("use_entity_config")
    private Boolean useEntityConfig;
    @JsonProperty("max_members")
    private MaxMembersDTO maxMembers;
    @JsonProperty("authenticators")
    private List<AuthenticatorDTO> authenticators;
    @JsonProperty("settings")
    private PortalSettingsDTO settings;


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getUseEntityConfig() {
        return useEntityConfig;
    }

    public void setUseEntityConfig(Boolean useEntityConfig) {
        this.useEntityConfig = useEntityConfig;
    }

    public MaxMembersDTO getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(MaxMembersDTO maxMembersDTO) {
        this.maxMembers = maxMembersDTO;
    }

    public List<AuthenticatorDTO> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(List<AuthenticatorDTO> authenticators) {
        this.authenticators = authenticators;
    }

    public PortalSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(PortalSettingsDTO settings) {
        this.settings = settings;
    }

}
