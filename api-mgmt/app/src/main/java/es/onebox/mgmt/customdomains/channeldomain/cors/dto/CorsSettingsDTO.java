package es.onebox.mgmt.customdomains.channeldomain.cors.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CorsSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 784116824929951748L;

    @NotNull(message = "Enabled field must not be null")
    private Boolean enabled;
    @JsonProperty("allowed_origins")
    @NotEmpty(message = "Allowed origins list must not be empty")
    @Size(max = 10, message = "Allowed origins list must not exceed 10 elements")
    @Valid
    private List<String> allowedOrigins;

    public CorsSettingsDTO() {
    }

    public CorsSettingsDTO(Boolean enabled, List<String> allowedOrigins) {
        this.enabled = enabled;
        this.allowedOrigins = allowedOrigins;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
