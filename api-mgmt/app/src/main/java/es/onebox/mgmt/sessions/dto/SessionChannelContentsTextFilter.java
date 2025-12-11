package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.common.channelcontents.ChannelContentTextFilter;
import es.onebox.mgmt.common.channelcontents.SessionChannelContentTextType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SessionChannelContentsTextFilter extends ChannelContentTextFilter<SessionChannelContentTextType>{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
