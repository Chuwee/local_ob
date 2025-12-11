package es.onebox.mgmt.channels.deliverymethods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class B2bExternalDownloadURLUpdateDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("target_channel_id")
    private Long targetChannelId;


    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getTargetChannelId() {
        return targetChannelId;
    }
    public void setTargetChannelId(Long targetChannel) {
        this.targetChannelId = targetChannel;
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
