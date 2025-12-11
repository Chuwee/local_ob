package es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.DomainFallbackConfigMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DomainFallbackConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    private Boolean enabled;
    @JsonProperty("channels_allowed")
    private DomainFallbackConfigMode channelsAllowedMode;
    private List<@Valid DomainFallbackConfigRuleDTO> rules;
    @Size(max = 255, message = "Redirect Url field must not exceed 255 characters")
    @JsonProperty("default_redirection_url")
    private String defaultRedirectionUrl;

    public DomainFallbackConfigDTO() {
    }

    public DomainFallbackConfigMode getChannelsAllowedMode() {
        return channelsAllowedMode;
    }

    public void setChannelsAllowedMode(DomainFallbackConfigMode channelsAllowedMode) {
        this.channelsAllowedMode = channelsAllowedMode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<DomainFallbackConfigRuleDTO> getRules() {
        return rules;
    }

    public void setRules(List<DomainFallbackConfigRuleDTO> rules) {
        this.rules = rules;
    }

    public String getDefaultRedirectionUrl() {
        return defaultRedirectionUrl;
    }

    public void setDefaultRedirectionUrl(String defaultRedirectionUrl) {
        this.defaultRedirectionUrl = defaultRedirectionUrl;
    }
}
