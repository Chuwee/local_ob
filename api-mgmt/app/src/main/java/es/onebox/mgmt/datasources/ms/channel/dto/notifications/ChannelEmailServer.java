package es.onebox.mgmt.datasources.ms.channel.dto.notifications;

import es.onebox.mgmt.datasources.ms.channel.enums.ChannelEmailServerType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class ChannelEmailServer implements Serializable {

    @Serial
    private static final long serialVersionUID = -7771242021117427326L;

    private ChannelEmailServerType type;
    private ChannelEmailServerConfiguration configuration;

    public ChannelEmailServerType getType() {
        return type;
    }

    public void setType(ChannelEmailServerType type) {
        this.type = type;
    }

    public ChannelEmailServerConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ChannelEmailServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
