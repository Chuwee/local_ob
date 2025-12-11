package es.onebox.mgmt.customdomains.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public class CustomDomainSetting implements Serializable {
    
    @Serial
    private static final long serialVersionUID = -2359364311769792331L;

    @NotNull(message = "Domain field must not be null")
    @Size(max = 255, message = "Domain field must not exceed 255 characters")
    private String domain;
    @JsonProperty("default")
    @NotNull(message = "Default field in domains list must not be null")
    private Boolean defaultDomain;

    public CustomDomainSetting() {
    }

    public CustomDomainSetting(String domain, Boolean defaultDomain) {
        this.domain = domain;
        this.defaultDomain = defaultDomain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getDefaultDomain() {
        return defaultDomain;
    }

    public void setDefaultDomain(Boolean defaultDomain) {
        this.defaultDomain = defaultDomain;
    }
}
