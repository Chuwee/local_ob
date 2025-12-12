package es.onebox.ms.notification.datasources.ms.channel.dto;


import es.onebox.ms.notification.datasources.ms.channel.domains.FacebookExternalToolParams;
import es.onebox.ms.notification.datasources.ms.channel.domains.GoogleExternalToolParams;
import es.onebox.ms.notification.datasources.ms.channel.enums.AdditionalConfigId;
import es.onebox.ms.notification.datasources.ms.channel.enums.ChannelExternalToolsNamesDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelExternalToolDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "name is mandatory")
    private ChannelExternalToolsNamesDTO name;
    private Boolean enabled;
    private Map<AdditionalConfigId, String> additionalConfig;
    @Size(max = 5, message = "A maximum of 5 parameter is allowed.")
    private List<FacebookExternalToolParams> sgtmFacebookCredentials;
    @Size(max = 5, message = "A maximum of 5 parameter is allowed.")
    private List<GoogleExternalToolParams> sgtmGoogleCredentials;

    public ChannelExternalToolDTO() {
        additionalConfig = new HashMap<>();
    }

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
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
