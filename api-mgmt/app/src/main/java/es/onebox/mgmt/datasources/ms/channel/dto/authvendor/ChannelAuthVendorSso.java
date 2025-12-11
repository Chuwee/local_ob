package es.onebox.mgmt.datasources.ms.channel.dto.authvendor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelAuthVendorSso implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allowed;

    public Boolean getAllowed() {
        return this.allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
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
