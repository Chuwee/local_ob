package es.onebox.mgmt.channels.notifications.dto;

import es.onebox.mgmt.channels.notifications.enums.EmailServerType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class ChannelEmailServerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3537537575563742158L;

    private EmailServerType type;
    private EmailServerConfiguration configuration;

    public EmailServerType getType() {
        return type;
    }

    public void setType(EmailServerType type) {
        this.type = type;
    }

    public EmailServerConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(EmailServerConfiguration configuration) {
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
