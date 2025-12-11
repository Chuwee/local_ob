package es.onebox.mgmt.datasources.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AuthConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Boolean enabled;
    private Boolean useEntityConfig;
    private MaxMembers maxMembers;
    private List<AuthenticationMethod> authenticationMethods;
    private PortalSettings settings;

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

    public MaxMembers getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(MaxMembers maxMembers) {
        this.maxMembers = maxMembers;
    }

    public List<AuthenticationMethod> getAuthenticationMethods() {
        return authenticationMethods;
    }

    public void setAuthenticationMethods(List<AuthenticationMethod> authenticationMethods) {
        this.authenticationMethods = authenticationMethods;
    }

    public PortalSettings getSettings() {
        return settings;
    }

    public void setSettings(PortalSettings portalSettings) {
        this.settings = portalSettings;
    }
}
