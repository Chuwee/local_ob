package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class B2bExternalDownloadURL implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Boolean enabled;
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
    public void setTargetChannelId(Long targetChannelId) {
        this.targetChannelId = targetChannelId;
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
