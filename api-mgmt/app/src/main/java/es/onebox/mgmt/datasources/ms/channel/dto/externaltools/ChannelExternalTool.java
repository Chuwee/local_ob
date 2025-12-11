package es.onebox.mgmt.datasources.ms.channel.dto.externaltools;

import es.onebox.mgmt.channels.externaltools.dto.FacebookExternalToolParams;
import es.onebox.mgmt.channels.externaltools.dto.GoogleExternalToolParams;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelExternalTool implements Serializable {

    private static final long serialVersionUID = 1L;

    private ChannelExternalToolsNames name;
    private Boolean enabled;
    private Map<ChannelExternalToolFieldIdentifier, String> additionalConfig;
    private List<FacebookExternalToolParams> sgtmFacebookCredentials;
    private List<GoogleExternalToolParams> sgtmGoogleCredentials;

    public ChannelExternalToolsNames getName() {
        return name;
    }

    public void setName(ChannelExternalToolsNames name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<ChannelExternalToolFieldIdentifier, String> getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(Map<ChannelExternalToolFieldIdentifier, String> additionalConfig) {
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
