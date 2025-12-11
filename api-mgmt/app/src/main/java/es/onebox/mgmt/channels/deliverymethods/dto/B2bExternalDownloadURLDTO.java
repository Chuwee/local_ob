package es.onebox.mgmt.channels.deliverymethods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class B2bExternalDownloadURLDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    private boolean enabled;
    @JsonProperty("target_channel")
    private B2bExternalTargetChannelDTO targetChannel;


    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public B2bExternalTargetChannelDTO getTargetChannel() {
        return targetChannel;
    }
    public void setTargetChannel(B2bExternalTargetChannelDTO targetChannel) {
        this.targetChannel = targetChannel;
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
