package es.onebox.mgmt.channels.blacklists.dto;

import es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelBlacklistStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ChannelBlacklistStatus status;

    public ChannelBlacklistStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelBlacklistStatus status) {
        this.status = status;
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