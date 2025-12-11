package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.common.channelcontents.SessionChannelContentImageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SessionChannelContentImageDTO extends CommunicationElementImageWithPositionDTO<SessionChannelContentImageType> {

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
