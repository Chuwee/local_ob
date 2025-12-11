package es.onebox.mgmt.datasources.ms.channel.dto.blacklists;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelBlacklistStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private BlacklistStatus status;

    public BlacklistStatus getStatus() {
        return status;
    }

    public void setStatus(BlacklistStatus status) {
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