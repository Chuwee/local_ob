package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class DonationsConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9075679882981897512L;
    @NotNull(message = "enabled can not be null")
    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("api_key")
    private String apiKey;
    @JsonProperty("provider_id")
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
