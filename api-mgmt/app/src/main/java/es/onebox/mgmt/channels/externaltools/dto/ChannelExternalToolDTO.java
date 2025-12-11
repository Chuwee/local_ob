package es.onebox.mgmt.channels.externaltools.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelExternalToolDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ChannelExternalToolsNamesDTO name;
    private Boolean enabled;
    @Valid
    @JsonProperty("additional_config")
    private List<ChannelExternalToolFieldDTO> additionalConfig;
    @JsonProperty("sgtm_facebook_credentials")
    @Size(max = 5, message = "A maximum of 5 parameter is allowed.")
    private List<FacebookExternalToolParams> sgtmFacebookCredentials;
    @JsonProperty("sgtm_google_credentials")
    @Size(max = 5, message = "A maximum of 5 parameter is allowed.")
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

    public List<ChannelExternalToolFieldDTO> getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(List<ChannelExternalToolFieldDTO> additionalConfig) {
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
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
