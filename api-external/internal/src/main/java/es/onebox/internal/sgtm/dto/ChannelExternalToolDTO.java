package es.onebox.internal.sgtm.dto;

import es.onebox.internal.sgtm.domains.FacebookExternalToolParams;
import es.onebox.internal.sgtm.domains.GoogleExternalToolParams;
import es.onebox.internal.sgtm.enums.AdditionalConfigId;
import es.onebox.internal.sgtm.enums.ChannelExternalToolsNamesDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelExternalToolDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotNull
    private ChannelExternalToolsNamesDTO name;
    
    private Boolean enabled;
    
    private Map<AdditionalConfigId, String> additionalConfig;
    
    @Size(min = 0)
    private List<FacebookExternalToolParams> sgtmFacebookCredentials;
    
    @Size(min = 0)
    private List<GoogleExternalToolParams> sgtmGoogleCredentials;

    public ChannelExternalToolsNamesDTO getName() {
        return name;
    }

    public void setName(ChannelExternalToolsNamesDTO name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<AdditionalConfigId, String> getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(Map<AdditionalConfigId, String> additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    public List<FacebookExternalToolParams> getSgtmFacebookCredentials() {
        return sgtmFacebookCredentials;
    }

    public void setSgtmFacebookCredentials(List<FacebookExternalToolParams> sgtmFacebookCredentials) {
        this.sgtmFacebookCredentials = sgtmFacebookCredentials;
    }

    public List<GoogleExternalToolParams> getSgtmGoogleCredentials() {
        return sgtmGoogleCredentials;
    }

    public void setSgtmGoogleCredentials(List<GoogleExternalToolParams> sgtmGoogleCredentials) {
        this.sgtmGoogleCredentials = sgtmGoogleCredentials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ChannelExternalToolDTO that = (ChannelExternalToolDTO) o;
        
        return new EqualsBuilder()
                .append(name, that.name)
                .append(enabled, that.enabled)
                .append(additionalConfig, that.additionalConfig)
                .append(sgtmFacebookCredentials, that.sgtmFacebookCredentials)
                .append(sgtmGoogleCredentials, that.sgtmGoogleCredentials)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(enabled)
                .append(additionalConfig)
                .append(sgtmFacebookCredentials)
                .append(sgtmGoogleCredentials)
                .toHashCode();
    }
}
