package es.onebox.mgmt.datasources.ms.entity.dto.user.realm;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class DonationsConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -1828285222118340356L;

    @NotNull(message = "enabled can not be null")
    private Boolean enabled;
    private String apiKey;
    private Long providerId;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}
